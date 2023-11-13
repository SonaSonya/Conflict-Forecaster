package com.conflict.forecaster.database;

import com.conflict.forecaster.database.entity.UCDPEventCount;
import org.springframework.beans.factory.annotation.Autowired;
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


    public List<UCDPEventCount> findCounts() {
        String sql = "select country_id, year, month, type_of_violence,  sum(deaths_all) as death_count, count(*) as violence_count from ucdpevent group by country_id, month, year, type_of_violence;";
        return jdbcTemplate.query(sql, new UCDPEventCountRowMapper());
    }

}
