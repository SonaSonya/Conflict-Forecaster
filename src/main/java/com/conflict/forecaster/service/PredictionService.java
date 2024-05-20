package com.conflict.forecaster.service;

import com.conflict.forecaster.config.ArimaConfig;
import com.conflict.forecaster.database.UCDPEventCountRepository;
import com.conflict.forecaster.database.entity.UCDPEventCount;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.workday.insights.timeseries.arima.Arima;
import com.workday.insights.timeseries.arima.struct.ForecastResult;
import com.workday.insights.timeseries.arima.struct.ArimaParams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class PredictionService {

    private UCDPEventCountRepository ucdpEventCountRepository;
    private ArimaParams arimaParams;

    @Autowired
    public PredictionService(UCDPEventCountRepository ucdpEventCountRepository, ArimaConfig arimaConfig) {
        this.ucdpEventCountRepository = ucdpEventCountRepository;
        this.arimaParams = arimaConfig.arimaConf();
    }

    public double[] getArrayOfViolenceCounts(List<UCDPEventCount> ucdpEventCounts) {

        UCDPEventCount firstEvent = ucdpEventCountRepository.findFirstByOrderByYearAscMonthAsc();
        UCDPEventCount lastEvent = ucdpEventCountRepository.findFirstByOrderByYearDescMonthDesc();

        int year = firstEvent.getYear();
        int month = firstEvent.getMonth();
        int lastYear = lastEvent.getYear();
        int lastMonth = lastEvent.getMonth();

        boolean hasEvent = false;
        int violenceCount = 0;

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

    protected ObjectNode getResponse(double[] forecastData) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode forecastsNode = mapper.createArrayNode();
        ObjectNode response = mapper.createObjectNode();

        int month = 0;

        for (double forecast : forecastData) {
            ObjectNode forecastNode = mapper.createObjectNode();
            forecastNode.put("month", month);
            forecastNode.put("amount", forecast);

            forecastsNode.add(forecastNode);
            month++;
        }

        response.set("forecasts", forecastsNode);

        return response;
    }

    public ObjectNode predict(int countryId, int violenceType, int timespan) {

        // Получение данных событий из базы данных
        List<UCDPEventCount> ucdpEventCounts = ucdpEventCountRepository.findByCountryIdAndTypeOfViolence(countryId,
                violenceType);

        // Подготовка массива данных
        double[] dataArray = getArrayOfViolenceCounts(ucdpEventCounts);

        // Установка параметров модели
        int forecastSize = timespan;

        // Обучение
        ForecastResult forecastResult = Arima.forecast_arima(dataArray, forecastSize, this.arimaParams);

        // Получение прогноза
        double[] forecastData = forecastResult.getForecast();

        return getResponse(forecastData);
    }

    public void gridSearch() {

        List<UCDPEventCount> ucdpEventCounts = ucdpEventCountRepository.findByCountryIdAndTypeOfViolence(530,
                1);

        // Prepare input timeseries data.
        double[] dataArray = getArrayOfViolenceCounts(ucdpEventCounts);
        System.out.println(Arrays.toString(dataArray));

        int P = 1;
        int D = 1;
        int Q = 0;
        int m = 0;
        int forecastSize = 6;

        double best = 10000000000.0;

        for (int p = 1; p < 6; p++) {
            for (int d = 0; d < 6; d++) {
                for (int q = 1; q < 6; q++) {

                    ForecastResult forecastResult = Arima.forecast_arima(dataArray, forecastSize,
                            new ArimaParams(p, d, q, P, D, Q, m));
                    double rmse = forecastResult.getRMSE();
                    if (best > rmse) {
                        best = rmse;
                        System.out.println(p + " " + d + " " + q + " " + rmse);
                    }
                }
            }
        }
    }
}
