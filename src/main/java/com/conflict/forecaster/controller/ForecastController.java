package com.conflict.forecaster.controller;

import com.conflict.forecaster.service.UCDPApiClient;
import com.conflict.forecaster.database.UCDPEventCountRepository;
import com.conflict.forecaster.database.UCDPEventRepository;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class ForecastController {

    @Autowired
    private UCDPApiClient api;
    @Autowired
    private UCDPEventRepository ucdpEventRepository;
    @Autowired
    private UCDPEventCountRepository ucdpEventCountRepository;

    // Сохранение событий ucdp в базу проекта
    @PostMapping("/forecaster/data/initialize")
    public String initialize(@RequestParam(name="start_date", required=true, defaultValue="18.0.1") String startDate,
                             @RequestParam(name="end_date", required=true, defaultValue="23.0.7") String endDate,
                             Model model) throws IOException, ParseException {

        //TODO: Если в базе уже есть данные, то они удаляются

        //api.saveEvents2018_2022();

        //TODO: На выходе количество записанных строк
        return "";
    }

    @PostMapping("/forecaster/data/update")
    public String update(@RequestParam(name="end_date", required=true) String endDate,
                         Model model) throws IOException, ParseException {

        //TODO: Определение последней записанной даты в базе
        //TODO: Добавление к базе новых значений по датам

        //TODO: На выходе количество записанных строк
        return "";
    }

    @GetMapping("/forecaster/data/status")
    public String status(Model model) {
        // Получение статуса по загруженным данным, какие даты и страны в наличии
        //TODO: start date, end date in database, for example ["19.0.5”, "23.0.6”], country ids with names, for example [553: Malawi, 432: Mali, 435: Mauritania]
        return "";
    }

    @GetMapping("/forecaster/predict")
    public String predict(@RequestParam(name="country_id", required=true) int countryId,
                          @RequestParam(name="timespan", required=true) int timespan,
                          @RequestParam(name="violence_type", required=true) int violenceType,
                          Model model) {

        // TODO: Получение предсказания для страны, типа насилия и временного промежутка

        // TODO: array of objects like { month: 2, amount: 34 } where amount is the number of violences
        return "";
    }

//    // Подсчет количества событий определенного типа и запись в базу данных
//    @GetMapping("/count-event")
//    public String countEvent(Model model) throws IOException, ParseException {
//
//        List<Object[]> counts = ucdpEventRepository.findCounts();
//        for (Object[] c : counts ) {
//            UCDPEventCount ucdpEventCount = new UCDPEventCount(((Long)c[0]).intValue(),(int)c[1] , (int)c[2], (int)c[3], ((BigDecimal)c[4]).intValue(), ((Long)c[5]).intValue());
//            ucdpEventCountRepository.save(ucdpEventCount);
//        }
//
//        return "parse-event";
//    }

//    @GetMapping("/predict")
//    public String predict(Model model) throws IOException, ParseException {
//
//        // Prepare input timeseries data.
//        double[] dataArray = new double[] {4, 6, 1, 1, 2, 3, 7, 3, 2, 1, 1, 1, 1, 0, 0, 2};
//
//        // Set ARIMA model parameters.
//        int p = 3;
//        int d = 0;
//        int q = 3;
//        int P = 1;
//        int D = 1;
//        int Q = 0;
//        int m = 0;
//        int forecastSize = 1;
//
//        ArimaParams params = new ArimaParams(p, d, q, P, D, Q, m);
//
//        // Obtain forecast result. The structure contains forecasted values and performance metric etc.
//        ForecastResult forecastResult = Arima.forecast_arima(dataArray, forecastSize, params);
//
//        // Read forecast values
//        double[] forecastData = forecastResult.getForecast(); // in this example, it will return { 2 }
//        System.out.println(forecastData[0]);
//
//        // You can obtain upper- and lower-bounds of confidence intervals on forecast values.
//        // By default, it computes at 95%-confidence level. This value can be adjusted in ForecastUtil.java
//        double[] uppers = forecastResult.getForecastUpperConf();
//        double[] lowers = forecastResult.getForecastLowerConf();
//
//        System.out.println(uppers[0]);
//        System.out.println(lowers[0]);
//
//        // You can also obtain the root mean-square error as validation metric.
//        double rmse = forecastResult.getRMSE();
//        System.out.println(rmse);
//
//        // It also provides the maximum normalized variance of the forecast values and their confidence interval.
//        double maxNormalizedVariance = forecastResult.getMaxNormalizedVariance();
//        System.out.println(maxNormalizedVariance);
//
//        // Finally you can read log messages.
//        String log = forecastResult.getLog();
//        System.out.println(log);
//
//        return "parse-event";
//    }

}
