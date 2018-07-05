package io.calculation;

import io.data.Ad;
import io.extra.ExtraProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdsAllocationImpl implements AdsAllocation {
    private double mainlinePriceThreshold;
    private double mainlineRankScoreThreshold;

    @Autowired
    public AdsAllocationImpl(ExtraProperty extraProperty) {
        this.mainlinePriceThreshold = extraProperty.getMainlinePriceThreshold();
        this.mainlineRankScoreThreshold = extraProperty.getMainlineRankScoreThreshold();
    }

    @Override
    public void allocateAds(List<Ad> ads) {
        for (Ad ad : ads) {
            ad.position = (ad.costPerClick >= mainlinePriceThreshold && ad.rankScore >= mainlineRankScoreThreshold) ? 1 : 2;
        }
    }
}
