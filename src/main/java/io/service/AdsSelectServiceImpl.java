package io.service;

import com.google.common.base.Strings;
import io.cache.CachePoolType;
import io.calculation.CTRModel;
import io.data.Ad;
import io.extra.FeatureType;
import io.extra.Utility;
import io.repository.AdsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AdsSelectServiceImpl implements AdsSelectService {

    private StringRedisTemplate cache;

    private AdsRepository adsRepository;

    private CTRModel ctrModel;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public AdsSelectServiceImpl(StringRedisTemplate cache, AdsRepository adsRepository, CTRModel ctrModel) {
        this.cache = cache;
        this.adsRepository = adsRepository;
        this.ctrModel = ctrModel;
    }

    @Override
    public List<Ad> selectAds(List<String> queryTerms, String deviceId, String deviceIp, String queryCategory) {
        List<Ad> ads = queryAds(queryTerms);

        String query = String.join(Utility.underscoreSeparator, queryTerms);

        return pClickCalculation(query, ads, deviceId, deviceIp, queryCategory);
    }

    @Override
    public boolean updateAds(Ad ad) {
        try {
            adsRepository.save(ad);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }

        return true;
    }

    private List<Ad> queryAds(List<String> queryTerms) {
        List<Ad> ads = new ArrayList<>();
        HashMap<Long, Integer> matchedAds = new HashMap<>();

        try {
            for (String queryTerm : queryTerms) {
                String queryKey = Utility.getCacheKey(queryTerm, CachePoolType.ad);

                Set<String> adIds = cache.opsForSet().members(queryKey);
                if (adIds != null && adIds.size() > 0) {
                    for (String adIdStr : adIds) {
                        Long adId = Long.parseLong(adIdStr);
                        if (matchedAds.containsKey(adId)) {
                            int count = matchedAds.get(adId) + 1;
                            matchedAds.put(adId, count);
                        } else {
                            matchedAds.put(adId, 1);
                        }
                    }
                }
            }

            for (Long adId : matchedAds.keySet()) {
                int adCount = matchedAds.get(adId);
                Optional<Ad> ad = adsRepository.findById(adId);
                if (ad.isPresent()) {
                    Ad adValue = ad.get();
                    adValue.relevanceScore = (double) (adCount / adValue.keywords.size());
                    ads.add(adValue);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }

        return ads;
    }

    private List<Ad> pClickCalculation(String query, List<Ad> ads, String deviceId, String deviceIp, String queryCategory) {
        try {
            for (Ad ad : ads) {
                List<Double> features = new ArrayList<>();

                String deviceIdClickKey = FeatureType.DEVICE_ID_CLICK.toString() + Utility.underscoreSeparator + deviceId;
                String deviceIdImpressionKey = FeatureType.DEVICE_ID_IMPRESSION.toString() + Utility.underscoreSeparator + deviceId;
                String deviceIpClickKey = FeatureType.DEVICE_IP_CLICK.toString() + Utility.underscoreSeparator + deviceIp;
                String deviceIpImpressionKey = FeatureType.DEVICE_IP_IMPRESSION.toString() + Utility.underscoreSeparator + deviceIp;
                String adIdClickKey = FeatureType.AD_ID_CLICK.toString() + Utility.underscoreSeparator + ad.adId;
                String adIdImpressionKey = FeatureType.AD_ID_IMPRESSION.toString() + Utility.underscoreSeparator + ad.adId;
                String queryCampaignIdClickKey = FeatureType.QUERY_CAMPAIGN_ID_CLICK.toString() + Utility.underscoreSeparator + query + Utility.underscoreSeparator + ad.campaignId;
                String queryCampaignIdImpressionKey = FeatureType.QUERY_CAMPAIGN_ID_IMPRESSION.toString() + Utility.underscoreSeparator + query + Utility.underscoreSeparator + ad.campaignId;
                String queryAdIdClick = FeatureType.QUERY_AD_ID_CLICK.toString() + Utility.underscoreSeparator + query + Utility.underscoreSeparator + ad.adId;
                String queryAdIdImpression = FeatureType.QUERY_AD_ID_IMPRESSION.toString() + Utility.underscoreSeparator + query + Utility.underscoreSeparator + ad.adId;

                List<String> featureKeys = Arrays.asList(deviceIdClickKey, deviceIdImpressionKey, deviceIpClickKey, deviceIpImpressionKey,
                        adIdClickKey, adIdImpressionKey, queryCampaignIdClickKey, queryCampaignIdImpressionKey, queryAdIdClick, queryAdIdImpression);

                for (String featureKey : featureKeys) {
                    String queryKey = Utility.getCacheKey(featureKey, CachePoolType.feature);

                    String featureStr = cache.opsForValue().get(queryKey);
                    if (!Strings.isNullOrEmpty(featureStr)) {
                        double featureVal = Double.parseDouble(featureStr);
                        features.add(featureVal);
                    }
                }

                // Last feature: Query Ad Category Match
                double queryAdCategoryMatch = queryCategory.equals(ad.category) ? 1000000.0 : 0.0;
                features.add(queryAdCategoryMatch);

                ad.pClick = ctrModel.predictCTR(features);
            }
        } catch (ClassCastException ce) {
            logger.warn(ce.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }

        return ads;
    }
}
