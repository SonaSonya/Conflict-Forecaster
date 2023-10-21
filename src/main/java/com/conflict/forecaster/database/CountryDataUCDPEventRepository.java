package com.conflict.forecaster.database;

import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CountryDataUCDPEventRepository
{
    List<CountryData> findCountries();
}
