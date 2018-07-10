package io.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "campaign")
public class Campaign implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "campaign_id")
    public long campaignId;
    public double budget;
}
