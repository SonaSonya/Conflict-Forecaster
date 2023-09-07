package com.conflict.forecaster.models.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class UCDPEventCount {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private int year, month, type_of_violence, violence_count, death_count, country_id;

    // Конструкторы
    public UCDPEventCount() {

    }

    public UCDPEventCount(int country_id, int year, int month, int type_of_violence, int death_count, int violence_count) {
        this.year = year;
        this.month = month;
        this.type_of_violence = type_of_violence;
        this.violence_count = violence_count;
        this.death_count = death_count;
        this.country_id = country_id;
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
        return type_of_violence;
    }

    public void setType_of_violence(int type_of_violence) {
        this.type_of_violence = type_of_violence;
    }

    public int getViolence_count() {
        return violence_count;
    }

    public void setViolence_count(int violence_count) {
        this.violence_count = violence_count;
    }

    public int getDeath_count() {
        return death_count;
    }

    public void setDeath_count(int death_count) {
        this.death_count = death_count;
    }

    public int getCountry_id() {
        return country_id;
    }

    public void setCountry_id(int country_id) {
        this.country_id = country_id;
    }
}
