package com.conflict.forecaster.service.predictor;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;

@Component
public class Predictor {

    private PredictionService predictionStrategy;

    public void setPredictionStrategy(PredictionService predictionService) {
        this.predictionStrategy = predictionService;
    }

    public ObjectNode predict(int countryId, int violenceType, int startYear, int startMonth, int lastYear, int lastMonth, int timespan) {
        return predictionStrategy.predict(countryId, violenceType, startYear, startMonth, lastYear, lastMonth, timespan);
    }

}
