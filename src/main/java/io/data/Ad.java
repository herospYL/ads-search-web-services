package io.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.extra.Utility;

import javax.persistence.*;
import java.io.Serializable;
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

    @Column(name = "key_words")
    @JsonIgnore
    public String keywordsStr;

    @Transient
    public List<String> keywords;

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

    @PostLoad
    public void keywordsLoad() {
        String[] keyWordsSplit = this.keywordsStr.split(Utility.commaSeparator);
        this.keywords = Arrays.asList(keyWordsSplit);
    }

    @PrePersist
    @PreUpdate
    public void keywordsUpdate() {
        this.keywordsStr = String.join(Utility.commaSeparator, this.keywords);
    }
}
