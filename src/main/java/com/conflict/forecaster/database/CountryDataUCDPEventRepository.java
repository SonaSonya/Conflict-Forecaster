package com.conflict.forecaster.database;

import com.conflict.forecaster.database.entity.UCDPEventCount;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CountryDataUCDPEventRepository
{
    List<CountryData> findCountries();

    List<UCDPEventCount> findCounts();
}
