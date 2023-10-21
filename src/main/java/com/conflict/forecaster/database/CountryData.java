package com.conflict.forecaster.database;

public class CountryData {

    private String country_id, country;

    public CountryData() {
    }

    public CountryData(String country_id, String country) {
        this.country_id = country_id;
        this.country = country;
    }

    public String getCountry_id() {
        return country_id;
    }

    public void setCountry_id(String country_id) {
        this.country_id = country_id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
