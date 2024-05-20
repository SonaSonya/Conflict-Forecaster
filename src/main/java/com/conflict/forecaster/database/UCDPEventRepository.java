package com.conflict.forecaster.database;

import com.conflict.forecaster.database.entity.UCDPEvent;
import com.conflict.forecaster.database.entity.UCDPEventCount;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UCDPEventRepository extends CrudRepository<UCDPEvent, Long>, CountryDataUCDPEventRepository {

    // Найти запись с самым поздним значением года и месяца
    List<UCDPEvent> findFirst1ByOrderByYearDescMonthDesc ();

    // Найти запись с самым ранним значением года и месяца
    List<UCDPEvent> findFirst1ByOrderByYearAscMonthAsc ();

}