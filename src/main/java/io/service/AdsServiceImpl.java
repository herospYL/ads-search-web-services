package io.service;

import com.google.common.base.Preconditions;
import io.calculation.AdsAllocation;
import io.calculation.AdsCalculation;
import io.calculation.CTRModel;
import io.calculation.QueryParser;
import io.data.Ad;
import io.extra.AdsFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AdsServiceImpl implements AdsService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private AdsInitializationService adsInitializationService;
    private AdsSelectService adsSelectService;
    private AdsCampaignService adsCampaignService;
    private CTRModel ctrModel;
    private QueryParser queryParser;
    private AdsFilter adsFilter;
    private AdsAllocation adsAllocation;

    @Autowired
    public AdsServiceImpl(AdsInitializationService adsInitializationService, AdsSelectService adsSelectService, AdsCampaignService adsCampaignService, CTRModel ctrModel, QueryParser queryParser, AdsFilter adsFilter, AdsAllocation adsAllocation) {
        this.adsInitializationService = adsInitializationService;
        this.adsSelectService = adsSelectService;
        this.adsCampaignService = adsCampaignService;
        this.ctrModel = ctrModel;
        this.queryParser = queryParser;
        this.adsFilter = adsFilter;
        this.adsAllocation = adsAllocation;
    }

    @Override
    public void initialize() throws IOException {
        adsInitializationService.initializeAds();
        logger.debug("Ads Initialization finished");
        adsInitializationService.initializeBudget();
        logger.debug("Budget Initialization finished");
        adsInitializationService.initializeFeature();
        logger.debug("Feature Initialization finished");
        adsInitializationService.initializeSynonym();
        logger.debug("Synonym Initialization finished");

        // CTRModel initialization is within its constructor, and will be called in AdsServiceImpl's constructor
    }

    @Override
    public List<Ad> selectAds(String query, String deviceId, String deviceIp, String queryCategory, int count) throws IOException {
        //query understanding
        List<List<String>> rewrittenQuery = queryParser.QueryRewrite(query);
        List<Ad> adsCandidates = new ArrayList<>();
        Set<Long> uniqueAdsIds = new HashSet<>();

        //select ads candidates
        for (List<String> queryTerms : rewrittenQuery) {
            List<Ad> adsCandidatesTemp = this.adsSelectService.selectAds(queryTerms, deviceId, deviceIp, queryCategory);
            for (Ad ad : adsCandidatesTemp) {
                if (!uniqueAdsIds.contains(ad.adId)) {
                    adsCandidates.add(ad);
                }
            }
        }

        //TODO: give rewritten query lower rank score

        //L0 filter by pClick, relevance score
        List<Ad> l0UnfilteredAds = adsFilter.levelZeroFilterAds(adsCandidates);
        logger.debug("L0unfilteredAds ads left = " + l0UnfilteredAds.size());

        //rank
        List<Ad> rankedAds = AdsCalculation.rankAds(l0UnfilteredAds);
        logger.debug("rankedAds ads left = " + rankedAds.size());

        //L1 filter by relevance score : select top K ads
        int k; // Outside method should specify the default count
        Preconditions.checkArgument(count >= 0);
        k = count;

        List<Ad> unfilteredAds = adsFilter.levelOneFilterAds(rankedAds, k);
        logger.debug("unfilteredAds ads left = " + unfilteredAds.size());

        //Dedupe ads per campaign
        List<Ad> dedupedAds = AdsCampaignServiceImpl.dedupeByCampaignId(unfilteredAds);
        logger.debug("dedupedAds ads left = " + dedupedAds.size());

        //pricing： next rank score/current score * current bid price
        AdsCalculation.setCostPerClick(dedupedAds);
        //filter last one , ad without budget , ads with CPC < minReservePrice
        List<Ad> ads = adsCampaignService.applyBudget(dedupedAds);
        logger.debug("AdsCampaignManager ads left = " + ads.size());

        //allocation
        adsAllocation.allocateAds(ads);
        return ads;
    }
}
