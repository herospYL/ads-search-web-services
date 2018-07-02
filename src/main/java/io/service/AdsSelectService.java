package io.service;

import io.data.Ad;

import java.util.List;

public interface AdsSelectService {
    List<Ad> selectAds(List<String> queryTerms, String deviceId, String deviceIp, String queryCategory);
    boolean updateAds(Ad ad);
}
