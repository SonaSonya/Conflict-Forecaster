package com.conflict.forecaster.service.predictor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;

@Component
public class PredictionResponse {

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
}
