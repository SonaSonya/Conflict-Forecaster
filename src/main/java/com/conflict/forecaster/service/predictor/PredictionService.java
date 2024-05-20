package com.conflict.forecaster.service.predictor;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface PredictionService {

    ObjectNode predict(int countryId, int violenceType, int startYear, int startMonth, int lastYear, int lastMonth, int timespan);
}
