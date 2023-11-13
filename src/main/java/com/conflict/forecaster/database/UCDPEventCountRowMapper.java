package com.conflict.forecaster.database;

import com.conflict.forecaster.database.entity.UCDPEventCount;
import jakarta.persistence.Column;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UCDPEventCountRowMapper implements RowMapper<UCDPEventCount> {

    @Override
    public UCDPEventCount mapRow(ResultSet rs, int rowNum) throws SQLException {

        UCDPEventCount UCDPEventCount = new UCDPEventCount();
        UCDPEventCount.setCountry_id(rs.getInt("country_id"));
        UCDPEventCount.setYear(rs.getInt("year"));
        UCDPEventCount.setMonth(rs.getInt("month"));
        UCDPEventCount.setType_of_violence(rs.getInt("type_of_violence"));
        UCDPEventCount.setDeath_count(rs.getInt("death_count"));
        UCDPEventCount.setViolence_count(rs.getInt("violence_count"));

        return UCDPEventCount;
    }

}
