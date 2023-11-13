package com.conflict.forecaster.database;

import com.conflict.forecaster.database.entity.UCDPEventCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UCDPEventCountRepository extends CrudRepository<UCDPEventCount, Long> {

    @Query("SELECT u FROM UCDPEventCount u WHERE u.country_id = :country_id AND u.type_of_violence = :type_of_violence ORDER BY u.year, u.month")
    List<UCDPEventCount> findByCountryIdAndTypeOfViolence(@Param("country_id") int countryId, @Param("type_of_violence") int typeOfViolence);

    UCDPEventCount findFirstByOrderByYearDescMonthDesc();
    UCDPEventCount findFirstByOrderByYearAscMonthAsc();

}
