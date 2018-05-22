package io.service;

import io.data.Ad;
import org.springframework.data.repository.CrudRepository;

public interface AdRepository extends CrudRepository<Ad, Long> {
    // Does not have special DB operations
}
