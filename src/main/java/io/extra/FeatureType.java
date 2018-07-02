package io.extra;

public enum FeatureType {
    DEVICE_ID_CLICK("didc"),
    DEVICE_ID_IMPRESSION("didi"),
    DEVICE_IP_CLICK("dipc"),
    DEVICE_IP_IMPRESSION("dipi"),
    AD_ID_CLICK("aidc"),
    AD_ID_IMPRESSION("aidi"),
    QUERY_CAMPAIGN_ID_CLICK("qcidc"),
    QUERY_CAMPAIGN_ID_IMPRESSION("qcidi"),
    QUERY_AD_ID_CLICK("qaidc"),
    QUERY_AD_ID_IMPRESSION("qaidi");

    private final String text;

    FeatureType(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
