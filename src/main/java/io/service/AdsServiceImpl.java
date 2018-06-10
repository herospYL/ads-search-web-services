package io.service;

import io.data.Ad;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdsServiceImpl implements AdsService{

    @Override
    public List<Ad> selectAds(List<String> queryTerms, String deviceId, String deviceIp, String queryCategory) {
        return null;
    }
}
