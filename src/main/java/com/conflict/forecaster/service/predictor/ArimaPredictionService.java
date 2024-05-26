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
    private ArimaParams arimaParams;
    private PredictionResponse predictionResponse;
    private PredictionDataPreparer predictionDataPreparer;

    @Autowired
    public ArimaPredictionService(ArimaConfig arimaConfig, PredictionResponse predictionResponse, PredictionDataPreparer predictionDataPreparer) {
        this.arimaParams = arimaConfig.arimaConf();
        this.predictionResponse = predictionResponse;
        this.predictionDataPreparer = predictionDataPreparer;
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
        double[] dataArray = predictionDataPreparer.getDataArray(countryId, violenceType, startYear, startMonth, lastYear, lastMonth, timespan);
        double[] allDataArray = predictionDataPreparer.getAllDataArray(countryId, violenceType, startYear, startMonth, lastYear, lastMonth, timespan);

        double[] forecastData = predictArima(dataArray, timespan);

        return predictionResponse.getResponse(forecastData, dataArray, allDataArray);
    }


    private double[] predictArima(double[] dataArray, int timespan) {

        // Установка параметров модели
        int forecastSize = timespan;

        // Обучение
        ForecastResult forecastResult = Arima.forecast_arima(dataArray, forecastSize, this.arimaParams);

        // Получение прогноза
        return forecastResult.getForecast();
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
