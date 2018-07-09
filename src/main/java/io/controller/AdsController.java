package io.controller;


import io.data.Ad;
import io.service.AdsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class AdsController {

    private AdsService adsService;

    @Autowired
    public AdsController(AdsService adsService) {
        this.adsService = adsService;
    }

    @RequestMapping("/ad")
    public List<Ad> getAds(@RequestParam(value = "query") String query, @RequestParam(value = "id") String deviceId,
                           @RequestHeader(value = "X-Forwarded-For", required = false, defaultValue = "1010") String deviceIp, @RequestParam(value = "class") String queryClass,
                           @RequestParam(value = "count", required = false, defaultValue = "5") int count) throws IOException {
        return this.adsService.selectAds(query, deviceId, deviceIp, queryClass, count);
    }
}

