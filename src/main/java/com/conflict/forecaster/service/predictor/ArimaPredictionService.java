package com.conflict.forecaster.service.predictor;

import com.conflict.forecaster.config.ArimaConfig;
import com.conflict.forecaster.database.UCDPEventCountRepository;
import com.conflict.forecaster.database.entity.UCDPEventCount;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.workday.insights.timeseries.arima.Arima;
import com.workday.insights.timeseries.arima.struct.ArimaParams;
import com.workday.insights.timeseries.arima.struct.ForecastResult;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ArimaPredictionService implements  PredictionService {

    private UCDPEventCountRepository ucdpEventCountRepository;
    private ArimaParams arimaParams;

    @Autowired
    public ArimaPredictionService(UCDPEventCountRepository ucdpEventCountRepository, ArimaConfig arimaConfig) {
        this.ucdpEventCountRepository = ucdpEventCountRepository;
        this.arimaParams = arimaConfig.arimaConf();
    }



    @Override
    public ObjectNode predict(
            int countryId,
            int violenceType,
            int startYear,
            int startMonth,
            int lastYear,
            int lastMonth,
            int timespan)
    {

        int[] endYearAndMonth = addMonths(lastYear, lastMonth, timespan);

        // Получение данных событий из базы данных
        List<UCDPEventCount> ucdpEventCounts = ucdpEventCountRepository.findByCountryIdAndTypeOfViolence(countryId,
                violenceType);

        // Подготовка массива данных
        double[] allDataArray = getArrayOfViolenceCounts(ucdpEventCounts,  startYear, startMonth, endYearAndMonth[0], endYearAndMonth[1]);
        double[] dataArray = getArrayOfViolenceCounts(ucdpEventCounts, startYear, startMonth, lastYear, lastMonth);

        double[] forecastData = predictArima(dataArray, timespan);

        return getResponse(forecastData, dataArray, allDataArray);
    }


    private double[] predictArima(double[] dataArray, int timespan) {

        // Установка параметров модели
        int forecastSize = timespan;

        // Обучение
        ForecastResult forecastResult = Arima.forecast_arima(dataArray, forecastSize, this.arimaParams);

        // Получение прогноза
        return forecastResult.getForecast();
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

    protected ObjectNode getResponse(double[] forecastData, double[] dataBeforeForecast, double[] actualData) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode forecastsNode = mapper.createArrayNode();
        ArrayNode actualNode = mapper.createArrayNode();
        ArrayNode dataWithForecastsNode = mapper.createArrayNode();
        ObjectNode response = mapper.createObjectNode();
        double[] dataWithForecasts = ArrayUtils.addAll(dataBeforeForecast, forecastData);

        int month = 0;

        for (double forecast : forecastData) {
            ObjectNode forecastNode = mapper.createObjectNode();
            forecastNode.put("month", month);
            forecastNode.put("amount", forecast);

            forecastsNode.add(forecastNode);
            month++;
        }

        for (int i = 0; i < actualData.length; i++) {
            actualNode.add(actualData[i]);
        }

        for (int i = 0; i < dataWithForecasts.length; i++) {
            dataWithForecastsNode.add(dataWithForecasts[i]);
        }

        response.set("forecasts", forecastsNode);
        response.set("dataWithForecasts", dataWithForecastsNode);
        response.set("actual", actualNode);

        return response;
    }

    public static int[] addMonths(int year, int month, int monthsToAdd) {
        month += monthsToAdd - 1;
        year += month / 12;
        month %= 12;
        month++;

        return new int[] {year, month};
    }

//    public void gridSearch() {
//
//        List<UCDPEventCount> ucdpEventCounts = ucdpEventCountRepository.findByCountryIdAndTypeOfViolence(530,
//                1);
//
//        // Prepare input timeseries data.
//        double[] dataArray = getArrayOfViolenceCounts(ucdpEventCounts);
//        System.out.println(Arrays.toString(dataArray));
//
//        int P = 1;
//        int D = 1;
//        int Q = 0;
//        int m = 0;
//        int forecastSize = 6;
//
//        double best = 10000000000.0;
//
//        for (int p = 1; p < 6; p++) {
//            for (int d = 0; d < 6; d++) {
//                for (int q = 1; q < 6; q++) {
//
//                    ForecastResult forecastResult = Arima.forecast_arima(dataArray, forecastSize,
//                            new ArimaParams(p, d, q, P, D, Q, m));
//                    double rmse = forecastResult.getRMSE();
//                    if (best > rmse) {
//                        best = rmse;
//                        System.out.println(p + " " + d + " " + q + " " + rmse);
//                    }
//                }
//            }
//        }
//    }
}
