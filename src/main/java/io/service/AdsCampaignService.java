package io.service;

import io.data.Ad;
import io.data.Campaign;

import java.util.List;

public interface AdsCampaignService {
    List<Ad> applyBudget(List<Ad> adsCandidates);

    boolean updateCampaign(Campaign campaign);
}
