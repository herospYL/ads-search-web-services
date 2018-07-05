package io.service;

import io.data.Ad;

import java.io.IOException;
import java.util.List;

public interface AdsService {
    void initialize() throws IOException;

    List<Ad> selectAds(String query, String deviceId, String deviceIp, String queryCategory, int count) throws IOException;
}
