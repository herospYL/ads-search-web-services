package io.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Strings;
import io.extra.Utility;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "ad")
public class Ad implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ad_id")
    public long adId;

    @Column(name = "campaign_id", nullable = false)
    public long campaignId;

    @Transient
    public double relevanceScore;

    @Transient
    public double pClick;

    @Transient
    public double rankScore;

    @Transient
    public double qualityScore;

    @Transient
    public double costPerClick;

    @Transient
    public int position; //1: top , 2: bottom

    @Column(name = "key_words")
    @JsonIgnore
    public String keywordsStr;

    @Column(name = "bid_price")
    public double bidPrice;

    @Column(nullable = false)
    public String title;

    @Column(nullable = false)
    public double price;

    @Column(nullable = false)
    public String thumbnail;

    @Column(nullable = false)
    public String description;

    @Column(nullable = false)
    public String brand;

    @Column(name = "detail_url", nullable = false)
    public String detailUrl;

    public String category;

    @Transient
    private List<String> keywords;

    public List<String> getKeywords() {
        if (this.keywords != null) {
            return this.keywords;
        }

        if (!Strings.isNullOrEmpty(this.keywordsStr)) {
            String[] keyWordsSplit = this.keywordsStr.split(Utility.commaSeparator);
            this.keywords = Arrays.asList(keyWordsSplit);
        }
        else {
            this.keywords = new ArrayList<>();
        }

        return this.keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
        this.keywordsStr = String.join(Utility.commaSeparator, this.keywords);
    }
}
