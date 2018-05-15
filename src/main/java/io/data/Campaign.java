package io.data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Campaign {

    @Id
    public long campaignId;
    public double budget;
}
