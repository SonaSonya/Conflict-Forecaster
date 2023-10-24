package com.conflict.forecaster.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class CountryDataUCDPEventRepositoryImpl implements CountryDataUCDPEventRepository
{

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CountryDataUCDPEventRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Вывести список стран
    public List<CountryData> findCountries()
    {
        String sql = "select distinct a.country_id, a.country from UCDPEvent a";

        return jdbcTemplate.query(sql, new CountryDataRowMapper());
    }
}
