package io.service;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.google.common.base.MoreObjects;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import io.cache.CachePoolType;
import io.data.Ad;
import io.data.Campaign;
import io.extra.Utility;
import io.repository.AdsRepository;
import io.repository.CampaignRepository;
import io.s3.InitializationProperty;
import io.s3.S3Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class AdsInitializationServiceImpl implements AdsInitializationService {
    private S3Property s3Property;

    private InitializationProperty initializationProperty;

    private AdsRepository adsRepository;

    private CampaignRepository campaignRepository;

    private StringRedisTemplate cache;

    private Utility utility;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private AmazonS3 s3Client;

    @Autowired
    public AdsInitializationServiceImpl(S3Property s3Property, InitializationProperty initializationProperty, AdsRepository adsRepository, CampaignRepository campaignRepository, StringRedisTemplate cache, Utility utility) {
        this.s3Property = s3Property;
        this.initializationProperty = initializationProperty;
        this.adsRepository = adsRepository;
        this.campaignRepository = campaignRepository;
        this.cache = cache;
        this.utility = utility;
        this.s3Client = AmazonS3ClientBuilder.standard().withRegion(s3Property.getRegions()).withCredentials(new ProfileCredentialsProvider()).build();
    }

    @Override
    public boolean initializeAds() throws IOException {
        S3Object s3Object = s3Client.getObject(s3Property.getBucket(), initializationProperty.getAdsFile());

        try (BufferedReader ctrReader = new BufferedReader(new InputStreamReader(s3Object.getObjectContent()))) {
            String line;
            while ((line = ctrReader.readLine()) != null) {
                Ad ad = parseAd(line);
                if (ad == null) continue;

                adsRepository.save(ad);
                indexingAd(ad); // indexing after write into DB
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }

        return true;
    }

    @Override
    public boolean initializeFeature() throws IOException {
        S3Object s3Object = s3Client.getObject(s3Property.getBucket(), initializationProperty.getFeatureFile());

        try (BufferedReader ctrReader = new BufferedReader(new InputStreamReader(s3Object.getObjectContent()))) {
            String line;
            while ((line = ctrReader.readLine()) != null) {
                DocumentContext json = JsonPath.parse(line);

                String featureKey = json.read("$.feature_key");
                String featureVal = json.read("$.feature_value");

                String queryKey = Utility.getCacheKey(featureKey, CachePoolType.feature);
                cache.opsForValue().set(queryKey, featureVal, initializationProperty.getCacheExp(), TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }

        return true;
    }

    @Override
    public boolean initializeSynonym() throws IOException {
        S3Object s3Object = s3Client.getObject(s3Property.getBucket(), initializationProperty.getSynonymFile());

        try  {
            DocumentContext json = JsonPath.parse(s3Object.getObjectContent()); // The entire file is one JSON

            Map<String, List<String>> synonyms = json.read("$");

            for (String synonymKey : synonyms.keySet()) {
                List<String> synonymVals = synonyms.get(synonymKey);

                String queryKey = Utility.getCacheKey(synonymKey, CachePoolType.synonyms);

                cache.opsForSet().union(queryKey, synonymVals);
                cache.expire(queryKey, initializationProperty.getCacheExp(), TimeUnit.SECONDS);
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }

        return true;
    }

    @Override
    public boolean initializeBudget() throws IOException {
        S3Object s3Object = s3Client.getObject(s3Property.getBucket(), initializationProperty.getBudgetFile());

        try (BufferedReader ctrReader = new BufferedReader(new InputStreamReader(s3Object.getObjectContent()))) {
            String line;
            while ((line = ctrReader.readLine()) != null) {
                Campaign campaign = parseBudget(line);

                campaignRepository.save(campaign);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }

        return true;
    }

    private Ad parseAd(String line) {
        DocumentContext json = JsonPath.parse(line);

        Ad ad = new Ad();

        Long adId = json.read("$.adId");
        Long campaignId = json.read("$.campaignId");

        if (adId == null || campaignId == null) {
            return null;
        }

        ad.adId = adId;
        ad.campaignId = campaignId;
        ad.brand = MoreObjects.firstNonNull(json.read("$.brand"), "");
        ad.price = MoreObjects.firstNonNull(json.read("$.price"), 100.0);
        ad.thumbnail = MoreObjects.firstNonNull(json.read("$.thumbnail"), "");
        ad.title = MoreObjects.firstNonNull(json.read("$.title"), "");
        ad.detailUrl = MoreObjects.firstNonNull(json.read("$.detail_url"), "");
        ad.bidPrice = MoreObjects.firstNonNull(json.read("$.bidPrice"), 1.0);
        ad.pClick = MoreObjects.firstNonNull(json.read("$.pClick"), 0.0);
        ad.category = MoreObjects.firstNonNull(json.read("$.category"), "");
        ad.description = MoreObjects.firstNonNull(json.read("$.description"), "");
        ad.keywords = MoreObjects.firstNonNull(json.read("$.keyWords"), new ArrayList<>()); // TODO: JSON uses keyWords instead

        return ad;
    }

    private Campaign parseBudget(String line) {
        DocumentContext json = JsonPath.parse(line);

        Campaign campaign = new Campaign();

        campaign.campaignId = json.read("$.campaignId");
        campaign.budget = json.read("$.budget");

        return campaign;
    }

    private void indexingAd(Ad ad) throws IOException {
        try {
            String keywordsStr = String.join(Utility.commaSeparator, ad.keywords);
            List<String> tokens = utility.cleanedTokenize(keywordsStr); // Ad's own method only triggers before persisting to DB

            for (String queryTerm : tokens) {
                String queryKey = Utility.getCacheKey(queryTerm, CachePoolType.ad);

                cache.opsForSet().add(queryKey, Long.toString(ad.adId));
                cache.expire(queryKey, initializationProperty.getCacheExp(), TimeUnit.SECONDS);
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw e;
        }
    }
}
