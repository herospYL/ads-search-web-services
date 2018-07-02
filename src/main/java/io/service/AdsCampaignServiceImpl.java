package io.service;

import io.data.Ad;
import io.data.Campaign;
import io.repository.CampaignRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AdsCampaignServiceImpl implements AdsCampaignService {

    private static double minPriceThreshold = 0.1;

    private CampaignRepository campaignRepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public AdsCampaignServiceImpl(CampaignRepository campaignRepository) {
        this.campaignRepository = campaignRepository;
    }

    public static List<Ad> dedupeByCampaignId(List<Ad> adsCandidates) {
        List<Ad> dedupedAds = new ArrayList<>();
        Set<Long> campaignIdSet = new HashSet<>();

        for (Ad ad : adsCandidates) {
            if (!campaignIdSet.contains(ad.campaignId)) {
                dedupedAds.add(ad);
                campaignIdSet.add(ad.campaignId);
            }
        }

        return dedupedAds;
    }

    @Override
    public List<Ad> applyBudget(List<Ad> adsCandidates) {
        List<Ad> ads = new ArrayList<>();
        try {
            // According to the algorithm, only select size - 1 in total
            for (int i = 0; i < adsCandidates.size() - 1; i++) {
                Ad ad = adsCandidates.get(i);
                long campaignId = ad.campaignId;

                Optional<Campaign> campaign = campaignRepository.findById(campaignId);
                if (campaign.isPresent()) {
                    // TODO: Logging
                    Campaign campaignVal = campaign.get();
                    if (ad.costPerClick <= campaignVal.budget && ad.costPerClick >= minPriceThreshold) {
                        ads.add(ad);
                        campaignVal.budget -= ad.costPerClick;
                        campaignRepository.save(campaignVal);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }

        return ads;
    }

    @Override
    public boolean updateCampaign(Campaign campaign) {
        try {
            campaignRepository.save(campaign);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }

        return true;
    }
}
