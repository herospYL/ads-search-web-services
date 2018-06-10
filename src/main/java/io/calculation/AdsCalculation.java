package io.calculation;

import io.data.Ad;

import java.util.List;

public class AdsCalculation {
    private static final double rankerCoefficient = 0.75;
    private static final double costPerClickCoefficient = 0.01;

    public static List<Ad> rankAds(List<Ad> adsCandidates) {
        for (Ad ad : adsCandidates) {
            ad.qualityScore = rankerCoefficient * ad.pClick + (1 - rankerCoefficient) * ad.relevanceScore;
            ad.rankScore = ad.qualityScore * ad.bidPrice;
        }

        // Sort by rank score large -> small
        adsCandidates.sort((one, two) -> Double.compare(two.rankScore, one.rankScore));

        return adsCandidates;
    }

    public static void setCostPerClick(List<Ad> adsCandidates) {
        for(int i = 0; i < adsCandidates.size(); i++) {
            if(i < adsCandidates.size() - 1) {
                adsCandidates.get(i).costPerClick = adsCandidates.get(i + 1).rankScore / adsCandidates.get(i).qualityScore + costPerClickCoefficient;
            }
            else {
                adsCandidates.get(i).costPerClick = adsCandidates.get(i).bidPrice;
            }
        }
    }
}
