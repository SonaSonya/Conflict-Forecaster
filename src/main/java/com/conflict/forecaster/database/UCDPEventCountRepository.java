package com.conflict.forecaster.database;

import com.conflict.forecaster.database.entity.UCDPEventCount;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UCDPEventCountRepository extends CrudRepository<UCDPEventCount, Long> {

}
