package io.extra;

import io.data.Ad;

import java.util.ArrayList;
import java.util.List;

public class AdsFilter {
    private double pClickThreshold;
    private double relevanceScoreThreshold;
    private int minNumOfAds;

    public AdsFilter(double pClickThreshold, double relevanceScoreThreshold, int minNumOfAds) {
        this.pClickThreshold = pClickThreshold;
        this.relevanceScoreThreshold = relevanceScoreThreshold;
        this.minNumOfAds = minNumOfAds;
    }

    public List<Ad> levelZeroFilterAds(List<Ad> adsCandidates)
    {
        if(adsCandidates.size() <= minNumOfAds)
            return adsCandidates;

        List<Ad> unfilteredAds = new ArrayList<>();
        for(Ad ad : adsCandidates)
        {
            if(ad.pClick >= pClickThreshold && ad.relevanceScore > relevanceScoreThreshold)
            {
                unfilteredAds.add(ad);
            }
        }
        return unfilteredAds;
    }

    public List<Ad> levelOneFilterAds(List<Ad> adsCandidates,int k)
    {
        if(adsCandidates.size() <= minNumOfAds)
            return adsCandidates;

        List<Ad> unfilteredAds = new ArrayList<>();
        for(int i = 0; i < Math.min(k, adsCandidates.size());i++)
        {
            unfilteredAds.add(adsCandidates.get(i));
        }
        return unfilteredAds;
    }
}
