package io.repository;

import io.data.Ad;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdsRepository extends CrudRepository<Ad, Long> {
    // Does not have special DB operations
}
