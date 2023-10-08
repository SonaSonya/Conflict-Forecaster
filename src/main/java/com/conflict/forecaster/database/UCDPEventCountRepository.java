package com.conflict.forecaster.database;

import com.conflict.forecaster.database.entity.UCDPEventCount;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UCDPEventCountRepository extends CrudRepository<UCDPEventCount, Long> {

    @Query("SELECT u FROM UCDPEventCount u WHERE u.countryId = :countryId AND u.typeOfViolence = :typeOfViolence ORDER BY u.year, u.month")
    List<UCDPEventCount> findByCountryIdAndTypeOfViolence(@Param("countryId") int countryId, @Param("typeOfViolence") int typeOfViolence);

    UCDPEventCount findFirstByOrderByYearDescMonthDesc();
    UCDPEventCount findFirstByOrderByYearAscMonthAsc();

}
