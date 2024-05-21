package com.conflict.forecaster.controller;

import com.conflict.forecaster.service.LSTM.LSTMPredictionService;
import com.conflict.forecaster.service.PredictionService;
import com.conflict.forecaster.service.UCDPApiClientService;
import com.conflict.forecaster.service.UCDPEventService;
import com.conflict.forecaster.service.predictor.ArimaPredictionService;
import com.conflict.forecaster.service.predictor.Predictor;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class ForecastController {

    private UCDPApiClientService api;
    private UCDPEventService ucdpEventService;
    private PredictionService predictionService;
    private LSTMPredictionService lstmPredictionService;
    private Predictor predictor;
    private ArimaPredictionService arimaPredictionService;
    @Autowired
    public ForecastController (
            UCDPApiClientService api,
            UCDPEventService ucdpEventService,
            LSTMPredictionService lstmPredictionService,
            Predictor predictor,
            ArimaPredictionService arimaPredictionService
    ) {
        this.api = api;
        this.ucdpEventService = ucdpEventService;
        this.lstmPredictionService = lstmPredictionService;
        this.predictor = predictor;
        this.arimaPredictionService = arimaPredictionService;
    }

    // Сохранение событий ucdp в базу проекта
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/forecaster/data/initialize")
    public ResponseEntity<Long> initialize(@RequestParam(name="start_date", required=true, defaultValue="18.0.1") String startDate,
                                             @RequestParam(name="end_date", required=true, defaultValue="23.0.7") String endDate) throws IOException, ParseException, InterruptedException {

        //System.out.println(startDate);
        //System.out.println(endDate);
        Long rowsSaved = api.initialize(startDate, endDate);
        //Thread.sleep(5000);
        //Long rowsSaved = Long.valueOf(1300);
        return new ResponseEntity<>(rowsSaved, HttpStatus.OK);
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/forecaster/data/update")
    public ResponseEntity<Long> update(@RequestParam(name="end_date", required=true) String endDate) throws IOException, ParseException, InterruptedException {
        //System.out.println(endDate);
        Long rowsSaved = api.update(endDate);
        //Thread.sleep(5000);
        //Long rowsSaved = Long.valueOf(1000);

        return new ResponseEntity<>(rowsSaved, HttpStatus.OK);
    }

    // Получение статуса по загруженным данным, какие даты и страны в наличии
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/forecaster/data/status")
    public ResponseEntity<ObjectNode> status() {
        ObjectNode status = ucdpEventService.getStatus();
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/forecaster/predict")
    public ResponseEntity<ObjectNode> predict(@RequestParam(name="country_id", required=true) int countryId,
        @RequestParam(name="violence_type", required=true) int violenceType,
        @RequestParam(name="timespan", required=true) int timespan,
        @RequestParam(name="start_year", required=true) int startYear,
        @RequestParam(name="start_month", required=true) int startMonth,
        @RequestParam(name="last_year", required=true) int lastYear,
        @RequestParam(name="last_month", required=true) int lastMonth,
        @RequestParam(name="model", required=true) String model
    ) {
        if (model.equals("arima")) {
            predictor.setPredictionStrategy(arimaPredictionService);
        }

        ObjectNode prediction = predictor.predict(countryId, violenceType, startYear, startMonth, lastYear, lastMonth, timespan);
        //ObjectNode prediction = predictionService.predict(countryId,violenceType,timespan);
        return new ResponseEntity<>(prediction, HttpStatus.OK);
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/test")
    public ResponseEntity<ObjectNode> test() {
        lstmPredictionService.test3();

        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/gridSearch")
    public ResponseEntity<ObjectNode> gridSearch() {
        predictionService.gridSearch();

        return new ResponseEntity<>(null, HttpStatus.OK);
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

}
