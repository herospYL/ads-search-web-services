package io.data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class Campaign implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    public long campaignId;
    public double budget;
}
