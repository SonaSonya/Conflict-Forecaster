package com.conflict.forecaster.service.predictor;

import com.conflict.forecaster.database.UCDPEventCountRepository;
import com.conflict.forecaster.database.entity.UCDPEventCount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PredictionDataPreparer {

    private UCDPEventCountRepository ucdpEventCountRepository;
    private List<UCDPEventCount> ucdpEventCounts;

    @Autowired
    public PredictionDataPreparer(UCDPEventCountRepository ucdpEventCountRepository) {
        this.ucdpEventCountRepository = ucdpEventCountRepository;
    }

    public double[] getAllDataArray(int countryId, int violenceType, int startYear, int startMonth, int lastYear, int lastMonth, int timespan) {
        int[] endYearAndMonth = addMonths(lastYear, lastMonth, timespan);

        // Получение данных событий из базы данных
        if (ucdpEventCounts == null) {
            ucdpEventCounts = ucdpEventCountRepository.findByCountryIdAndTypeOfViolence(countryId, violenceType);
        }
        return getArrayOfViolenceCounts(ucdpEventCounts,  startYear, startMonth, endYearAndMonth[0], endYearAndMonth[1]);
    }

    public double[] getDataArray(int countryId, int violenceType, int startYear, int startMonth, int lastYear, int lastMonth, int timespan) {
        // Получение данных событий из базы данных
        if (ucdpEventCounts == null) {
            ucdpEventCounts = ucdpEventCountRepository.findByCountryIdAndTypeOfViolence(countryId, violenceType);
        }
        return getArrayOfViolenceCounts(ucdpEventCounts, startYear, startMonth, lastYear, lastMonth);
    }

    private double[] getArrayOfViolenceCounts(List<UCDPEventCount> ucdpEventCounts, int startYear, int startMonth, int lastYear, int lastMonth) {
        boolean hasEvent = false;
        int violenceCount = 0;
        int year = startYear;
        int month = startMonth;

        ArrayList<Integer> violenceCounts = new ArrayList<>();

        while (year < lastYear || (year == lastYear && month <= lastMonth)) {

            for (UCDPEventCount ucdpEventCount : ucdpEventCounts) {
                if (ucdpEventCount.getMonth() == month && ucdpEventCount.getYear() == year) {
                    hasEvent = true;
                    violenceCount = ucdpEventCount.getViolence_count();
                    break;
                }
            }

            if (hasEvent) {
                violenceCounts.add(violenceCount);
            } else {
                violenceCounts.add(0);
            }

            hasEvent = false;
            violenceCount = 0;
            if (month >= 12) {
                month = 1;
                year++;
            } else {
                month++;
            }
        }

        return violenceCounts.stream().mapToDouble(i -> i).toArray();
    }

    public static int[] addMonths(int year, int month, int monthsToAdd) {
        month += monthsToAdd - 1;
        year += month / 12;
        month %= 12;
        month++;

        return new int[] {year, month};
    }


}
