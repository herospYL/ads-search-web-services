package io.s3;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("init")
public class InitializationProperty {
    private String adsFile;
    private String featureFile;
    private String synonymFile;
    private String budgetFile;
    private String ctrFile;
    private int cacheExp;

    public String getAdsFile() {
        return adsFile;
    }

    public void setAdsFile(String adsFile) {
        this.adsFile = adsFile;
    }

    public String getFeatureFile() {
        return featureFile;
    }

    public void setFeatureFile(String featureFile) {
        this.featureFile = featureFile;
    }

    public String getSynonymFile() {
        return synonymFile;
    }

    public void setSynonymFile(String synonymFile) {
        this.synonymFile = synonymFile;
    }

    public String getBudgetFile() {
        return budgetFile;
    }

    public void setBudgetFile(String budgetFile) {
        this.budgetFile = budgetFile;
    }

    public int getCacheExp() {
        return cacheExp;
    }

    public void setCacheExp(int cacheExp) {
        this.cacheExp = cacheExp;
    }

    public String getCtrFile() {
        return ctrFile;
    }

    public void setCtrFile(String ctrFile) {
        this.ctrFile = ctrFile;
    }
}
