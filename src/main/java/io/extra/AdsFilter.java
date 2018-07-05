package io.extra;

import io.data.Ad;

import java.util.List;

public interface AdsFilter {
    List<Ad> levelZeroFilterAds(List<Ad> adsCandidates);

    List<Ad> levelOneFilterAds(List<Ad> adsCandidates, int k);
}
