package com.conflict.forecaster.database;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CountryDataRowMapper implements RowMapper<CountryData> {

    @Override
    public CountryData mapRow(ResultSet rs, int rowNum) throws SQLException {

        CountryData countryData = new CountryData();
        countryData.setCountry_id(rs.getString("country_id"));
        countryData.setCountry(rs.getString("country"));

        return countryData;
    }
}
