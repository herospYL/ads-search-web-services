package io.service;

import io.data.Ad;

import java.util.List;

public interface AdsService {
    List<Ad> selectAds(List<String> queryTerms, String deviceId, String deviceIp, String queryCategory);
}
