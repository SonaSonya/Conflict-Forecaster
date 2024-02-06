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
import java.util.List;

@Service
public class PredictionService {

    private UCDPEventCountRepository ucdpEventCountRepository;
    private ArimaParams arimaParams;
    @Autowired
    public PredictionService(UCDPEventCountRepository ucdpEventCountRepository, ArimaConfig arimaConfig){
        this.ucdpEventCountRepository = ucdpEventCountRepository;
        this.arimaParams = arimaConfig.arimaConf();
    }

    public double[] getArrayOfViolenceCounts (List<UCDPEventCount> ucdpEventCounts) {

        UCDPEventCount firstEvent = ucdpEventCountRepository.findFirstByOrderByYearAscMonthAsc();
        UCDPEventCount lastEvent = ucdpEventCountRepository.findFirstByOrderByYearDescMonthDesc();

        int year = firstEvent.getYear();
        int month = firstEvent.getMonth();
        int lastYear = lastEvent.getYear();
        int lastMonth = lastEvent.getMonth();

        boolean hasEvent = false;
        int violenceCount = 0;

        ArrayList<Integer> violenceCounts = new ArrayList<>();

        while ( year < lastYear || ( year == lastYear && month <= lastMonth ) ) {

            for (UCDPEventCount ucdpEventCount : ucdpEventCounts)
            {
                if (ucdpEventCount.getMonth() == month && ucdpEventCount.getYear() == year) {
                    hasEvent = true;
                    violenceCount = ucdpEventCount.getViolence_count();
                    break;
                }
            }

            if (hasEvent) {
                violenceCounts.add(violenceCount);
            }
            else {
                violenceCounts.add(0);
            }

            hasEvent = false;
            violenceCount = 0;
            if (month >= 12) {
                month = 1;
                year++;
            }
            else {
                month++;
            }
        }

        return violenceCounts.stream().mapToDouble(i->i).toArray();
    }

    protected ObjectNode getResponse (double[] forecastData) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode forecastsNode = mapper.createArrayNode();
        ObjectNode response = mapper.createObjectNode();

        int month = 0;

        for (double forecast : forecastData)
        {
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

        List<UCDPEventCount> ucdpEventCounts = ucdpEventCountRepository.findByCountryIdAndTypeOfViolence(countryId, violenceType);

        // Prepare input timeseries data.
        double[] dataArray = getArrayOfViolenceCounts(ucdpEventCounts);

        // Set ARIMA model parameters.
        int forecastSize = timespan;

        // Obtain forecast result. The structure contains forecasted values and performance metric etc.
        ForecastResult forecastResult = Arima.forecast_arima(dataArray, forecastSize, this.arimaParams);

        // Read forecast values
        double[] forecastData = forecastResult.getForecast();

        return getResponse(forecastData);
    }

}
