package com.conflict.forecaster.database.entity;

import jakarta.persistence.*;

@Entity
public class UCDPEvent {

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;

        private Long id_ucdp;

        private int year, month, type_of_violence;
        private String conflict_name, dyad_name, side_a, side_b, adm_1, adm_2, country, region, date_start, date_end;
        private Long conflict_id, dyad_id, side_a_id, side_b_id, priogrid_gid, country_id, date_prec;
        private Long deaths_a, deaths_b, deaths_civilians, deaths_unknown, deaths_all;
        private double latitude, longitude;

        // Конструкторы
        public UCDPEvent() {
        }

        public UCDPEvent(Long id_ucdp, int year, int month, int type_of_violence, String conflict_name, String dyad_name, String side_a, String side_b, String adm_1, String adm_2, String country, String region, String date_start, String date_end, Long conflict_id, Long dyad_id, Long side_a_id, Long side_b_id, Long priogrid_gid, Long country_id, Long date_prec, Long deaths_a, Long deaths_b, Long deaths_civilians, Long deaths_unknown, Long deaths_all, double latitude, double longitude) {
                this.id = id;
                this.id_ucdp = id_ucdp;
                this.year = year;
                this.month = month;
                this.type_of_violence = type_of_violence;
                this.conflict_name = conflict_name;
                this.dyad_name = dyad_name;
                this.side_a = side_a;
                this.side_b = side_b;
                this.adm_1 = adm_1;
                this.adm_2 = adm_2;
                this.country = country;
                this.region = region;
                this.date_start = date_start;
                this.date_end = date_end;
                this.conflict_id = conflict_id;
                this.dyad_id = dyad_id;
                this.side_a_id = side_a_id;
                this.side_b_id = side_b_id;
                this.priogrid_gid = priogrid_gid;
                this.country_id = country_id;
                this.date_prec = date_prec;
                this.deaths_a = deaths_a;
                this.deaths_b = deaths_b;
                this.deaths_civilians = deaths_civilians;
                this.deaths_unknown = deaths_unknown;
                this.deaths_all = deaths_all;
                this.latitude = latitude;
                this.longitude = longitude;
        }

        // Геттеры и сеттеры
        public Long getId() {
                return id;
        }

        public void setId(Long id) {
                this.id = id;
        }

        public Long getId_ucdp() {
                return id_ucdp;
        }

        public void setId_ucdp(Long id_ucdp) {
                this.id_ucdp = id_ucdp;
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

        public String getConflict_name() {
                return conflict_name;
        }

        public void setConflict_name(String conflict_name) {
                this.conflict_name = conflict_name;
        }

        public String getDyad_name() {
                return dyad_name;
        }

        public void setDyad_name(String dyad_name) {
                this.dyad_name = dyad_name;
        }

        public String getSide_a() {
                return side_a;
        }

        public void setSide_a(String side_a) {
                this.side_a = side_a;
        }

        public String getSide_b() {
                return side_b;
        }

        public void setSide_b(String side_b) {
                this.side_b = side_b;
        }

        public String getAdm_1() {
                return adm_1;
        }

        public void setAdm_1(String adm_1) {
                this.adm_1 = adm_1;
        }

        public String getAdm_2() {
                return adm_2;
        }

        public void setAdm_2(String adm_2) {
                this.adm_2 = adm_2;
        }

        public String getCountry() {
                return country;
        }

        public void setCountry(String country) {
                this.country = country;
        }

        public String getRegion() {
                return region;
        }

        public void setRegion(String region) {
                this.region = region;
        }

        public String getDate_start() {
                return date_start;
        }

        public void setDate_start(String date_start) {
                this.date_start = date_start;
        }

        public String getDate_end() {
                return date_end;
        }

        public void setDate_end(String date_end) {
                this.date_end = date_end;
        }

        public Long getConflict_id() {
                return conflict_id;
        }

        public void setConflict_id(Long conflict_id) {
                this.conflict_id = conflict_id;
        }

        public Long getDyad_id() {
                return dyad_id;
        }

        public void setDyad_id(Long dyad_id) {
                this.dyad_id = dyad_id;
        }

        public Long getSide_a_id() {
                return side_a_id;
        }

        public void setSide_a_id(Long side_a_id) {
                this.side_a_id = side_a_id;
        }

        public Long getSide_b_id() {
                return side_b_id;
        }

        public void setSide_b_id(Long side_b_id) {
                this.side_b_id = side_b_id;
        }

        public Long getPriogrid_gid() {
                return priogrid_gid;
        }

        public void setPriogrid_gid(Long priogrid_gid) {
                this.priogrid_gid = priogrid_gid;
        }

        public Long getCountry_id() {
                return country_id;
        }

        public void setCountry_id(Long country_id) {
                this.country_id = country_id;
        }

        public Long getDate_prec() {
                return date_prec;
        }

        public void setDate_prec(Long date_prec) {
                this.date_prec = date_prec;
        }

        public Long getDeaths_a() {
                return deaths_a;
        }

        public void setDeaths_a(Long deaths_a) {
                this.deaths_a = deaths_a;
        }

        public Long getDeaths_b() {
                return deaths_b;
        }

        public void setDeaths_b(Long deaths_b) {
                this.deaths_b = deaths_b;
        }

        public Long getDeaths_civilians() {
                return deaths_civilians;
        }

        public void setDeaths_civilians(Long deaths_civilians) {
                this.deaths_civilians = deaths_civilians;
        }

        public Long getDeaths_unknown() {
                return deaths_unknown;
        }

        public void setDeaths_unknown(Long deaths_unknown) {
                this.deaths_unknown = deaths_unknown;
        }

        public Long getDeaths_all() {
                return deaths_all;
        }

        public void setDeaths_all(Long deaths_all) {
                this.deaths_all = deaths_all;
        }

        public double getLatitude() {
                return latitude;
        }

        public void setLatitude(double latitude) {
                this.latitude = latitude;
        }

        public double getLongitude() {
                return longitude;
        }

        public void setLongitude(double longitude) {
                this.longitude = longitude;
        }
}
