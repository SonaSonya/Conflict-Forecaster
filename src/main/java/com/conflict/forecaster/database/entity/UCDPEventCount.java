package com.conflict.forecaster.database.entity;

import jakarta.persistence.*;

@Entity
public class UCDPEventCount {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private int year, month;
    @Column(name="type_of_violence")
    private int typeOfViolence;
    @Column(name="violence_count")
    private int violenceCount;
    @Column(name="death_count")
    private int deathCount;
    @Column(name="country_id")
    private int countryId;

    // Конструкторы
    public UCDPEventCount() {

    }

    public UCDPEventCount(int country_id, int year, int month, int type_of_violence, int death_count, int violence_count) {
        this.year = year;
        this.month = month;
        this.typeOfViolence = type_of_violence;
        this.violenceCount = violence_count;
        this.deathCount = death_count;
        this.countryId = country_id;
    }

    // Геттеры и сеттеры

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getType_of_violence() {
        return typeOfViolence;
    }

    public void setType_of_violence(int type_of_violence) {
        this.typeOfViolence = type_of_violence;
    }

    public int getViolence_count() {
        return violenceCount;
    }

    public void setViolence_count(int violence_count) {
        this.violenceCount = violence_count;
    }

    public int getDeath_count() {
        return deathCount;
    }

    public void setDeath_count(int death_count) {
        this.deathCount = death_count;
    }

    public int getCountry_id() {
        return countryId;
    }

    public void setCountry_id(int country_id) {
        this.countryId = country_id;
    }
}
