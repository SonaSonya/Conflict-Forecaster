package com.conflict.forecaster.controllers;

import com.conflict.forecaster.models.UCDPApiClient;
import com.conflict.forecaster.models.entities.UCDPEventCount;
import com.conflict.forecaster.repo.UCDPEventCountRepository;
import com.conflict.forecaster.repo.UCDPEventRepository;
import com.workday.insights.timeseries.arima.struct.ArimaParams;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import com.workday.insights.timeseries.arima.Arima;
import com.workday.insights.timeseries.arima.struct.ForecastResult;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @Autowired
    private UCDPApiClient api;
    @Autowired
    private UCDPEventRepository ucdpEventRepository;
    @Autowired
    private UCDPEventCountRepository ucdpEventCountRepository;

    // тест работы Spring и шаблонизатора
    @GetMapping("/greeting")
    public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        return "greeting";
    }

    // Сохранение событий ucdp в базу проекта
    @GetMapping("/parse-event")
    public String parseEvent(@RequestParam(name="countryId", required=false, defaultValue="501") String countryId, Model model) throws IOException, ParseException {

        api.saveEvents2018_2022();

        return "parse-event";
    }

    // Подсчет количества событий определенного типа и запись в базу данных
    @GetMapping("/count-event")
    public String countEvent(Model model) throws IOException, ParseException {

        List<Object[]> counts = ucdpEventRepository.findCounts();
        for (Object[] c : counts ) {
            UCDPEventCount ucdpEventCount = new UCDPEventCount(((Long)c[0]).intValue(),(int)c[1] , (int)c[2], (int)c[3], ((BigDecimal)c[4]).intValue(), ((Long)c[5]).intValue());
            ucdpEventCountRepository.save(ucdpEventCount);
        }

        return "parse-event";
    }

    @GetMapping("/predict")
    public String predict(Model model) throws IOException, ParseException {

        // Prepare input timeseries data.
        double[] dataArray = new double[] {4, 6, 1, 1, 2, 3, 7, 3, 2, 1, 1, 1, 1, 0, 0, 2};

        // Set ARIMA model parameters.
        int p = 3;
        int d = 0;
        int q = 3;
        int P = 1;
        int D = 1;
        int Q = 0;
        int m = 0;
        int forecastSize = 1;

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

        return "parse-event";
    }







}
