package io.data;

import io.extra.Utility;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Entity
public class Ad implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    public long adId;

    public long campaignId;

    @Transient
    public List<String> keyWords;

    public String keyWordsStr;

    public double relevanceScore;

    public double pClick;

    public double bidPrice;

    public double rankScore;

    public double qualityScore;

    public double costPerClick;

    public int position; //1: top , 2: bottom

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

    @Column(nullable = false)
    public String detail_url;

    @Column(nullable = false)
    public String query;

    public String category;

    @PostLoad
    public void keyWordsLoad(){
        String[] keyWordsSplit = this.keyWordsStr.split(Utility.Separator);
        this.keyWords = Arrays.asList(keyWordsSplit);
    }

    @PrePersist
    @PreUpdate
    public void keyWordsUpdate(){
        this.keyWordsStr = String.join(Utility.Separator, this.keyWords);
    }
}
