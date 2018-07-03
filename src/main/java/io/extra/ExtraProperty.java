package io.extra;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("extra")
public class ExtraProperty {
    private String stopWords; // Will translate to snake style: stop-words
    private double pClickThreshold;
    private double relevanceScoreThreshold;
    private int minNumOfAds;

    public String getStopWords() {
        return stopWords;
    }

    public double getPClickThreshold() {
        return pClickThreshold;
    }

    public double getRelevanceScoreThreshold() {
        return relevanceScoreThreshold;
    }

    public int getMinNumOfAds() {
        return minNumOfAds;
    }

    public void setPClickThreshold(double pClickThreshold) {
        this.pClickThreshold = pClickThreshold;
    }

    public void setRelevanceScoreThreshold(double relevanceScoreThreshold) {
        this.relevanceScoreThreshold = relevanceScoreThreshold;
    }

    public void setMinNumOfAds(int minNumOfAds) {
        this.minNumOfAds = minNumOfAds;
    }

    public void setStopWords(String stopWords) {
        this.stopWords = stopWords;
    }
}
