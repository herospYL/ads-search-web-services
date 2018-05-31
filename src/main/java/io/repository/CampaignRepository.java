package io.repository;

import io.data.Campaign;
import org.springframework.data.repository.CrudRepository;

public interface CampaignRepository extends CrudRepository<Campaign, Long> {
    // Does not have special DB operations
}
