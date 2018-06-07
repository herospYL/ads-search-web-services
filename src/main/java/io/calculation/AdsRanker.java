package io.calculation;

import io.data.Ad;

import java.util.List;

public class AdsRanker {
    private static double coefficient = 0.75;

    public static List<Ad> rankAds(List<Ad> adsCandidates) {
        for (Ad ad : adsCandidates) {
            ad.qualityScore = coefficient * ad.pClick + (1 - coefficient) * ad.relevanceScore;
            ad.rankScore = ad.qualityScore * ad.bidPrice;
        }

        // Sort by rank score large -> small
        adsCandidates.sort((one, two) -> Double.compare(two.rankScore, one.rankScore));

        return adsCandidates;
    }
}
