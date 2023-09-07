package com.conflict.forecaster.repo;

import com.conflict.forecaster.models.entities.UCDPEventCount;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UCDPEventCountRepository extends CrudRepository<UCDPEventCount, Long> {

}
