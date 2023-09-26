package com.conflict.forecaster.database;

import com.conflict.forecaster.database.entity.UCDPEvent;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UCDPEventRepository extends CrudRepository<UCDPEvent, Long> {

    // Запрос подсчета количества событий из базы
    @Query(value = "select country_id, \"year\", \"month\", \"type_of_violence\",  sum(deaths_all), count(*) from ucdpevent \n" +
            "group by country_id, \"month\", \"year\", \"type_of_violence\";", nativeQuery = true)
    List<Object[]> findCounts();

}