package com.conflict.forecaster.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.workday.insights.timeseries.arima.Arima;
import com.workday.insights.timeseries.arima.struct.ForecastResult;
import com.workday.insights.timeseries.arima.struct.ArimaParams;
import org.springframework.web.bind.annotation.RequestParam;

@Service
public class PredictionService {

    @Value("${arima.parameters.p}")
    private int p;
    @Value("${arima.parameters.d}")
    private int d;
    @Value("${arima.parameters.q}")
    private int q;
    @Value("${arima.parameters.P}")
    private int P;
    @Value("${arima.parameters.D}")
    private int D;
    @Value("${arima.parameters.Q}")
    private int Q;
    @Value("${arima.parameters.m}")
    private int m;

    public String predict(int countryId, int violenceType, int timespan) {

        // Prepare input timeseries data.
        double[] dataArray = new double[] {4, 6, 1, 1, 2, 3, 7, 3, 2, 1, 1, 1, 1, 0, 0, 2};

        // Set ARIMA model parameters.
        int forecastSize = timespan;

        ArimaParams params = new ArimaParams(p, d, q, P, D, Q, m);

        // Obtain forecast result. The structure contains forecasted values and performance metric etc.
        ForecastResult forecastResult = Arima.forecast_arima(dataArray, forecastSize, params);

        // Read forecast values
        double[] forecastData = forecastResult.getForecast(); // in this example, it will return { 2 }
        System.out.println(forecastData[0]);

        // You can obtain upper- and lower-bounds of confidence intervals on forecast values.
        // By default, it computes at 95%-confidence level. This value can be adjusted in ForecastUtil.java
        double[] uppers = forecastResult.getForecastUpperConf();
        double[] lowers = forecastResult.getForecastLowerConf();

        System.out.println(uppers[0]);
        System.out.println(lowers[0]);

        // You can also obtain the root mean-square error as validation metric.
        double rmse = forecastResult.getRMSE();
        System.out.println(rmse);

        // It also provides the maximum normalized variance of the forecast values and their confidence interval.
        double maxNormalizedVariance = forecastResult.getMaxNormalizedVariance();
        System.out.println(maxNormalizedVariance);

        // Finally you can read log messages.
        String log = forecastResult.getLog();
        System.out.println(log);

        // TODO: array of objects like { month: 2, amount: 34 } where amount is the number of violences
        return "parse-event";
    }

}
