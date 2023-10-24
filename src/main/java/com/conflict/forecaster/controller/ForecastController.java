package com.conflict.forecaster.controller;

import com.conflict.forecaster.service.PredictionService;
import com.conflict.forecaster.service.UCDPApiClientService;
import com.conflict.forecaster.database.UCDPEventCountRepository;
import com.conflict.forecaster.database.UCDPEventRepository;
import com.conflict.forecaster.service.UCDPEventService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class ForecastController {

    private UCDPApiClientService api;
    private UCDPEventService ucdpEventService;
    private PredictionService predictionService;

    @Autowired
    public ForecastController (UCDPApiClientService api, UCDPEventService ucdpEventService, PredictionService predictionService) {
        this.api = api;
        this.ucdpEventService = ucdpEventService;
        this.predictionService = predictionService;
    }

    // Сохранение событий ucdp в базу проекта
    @PostMapping("/forecaster/data/initialize")
    public ResponseEntity<Long> initialize(@RequestParam(name="start_date", required=true, defaultValue="18.0.1") String startDate,
                                             @RequestParam(name="end_date", required=true, defaultValue="23.0.7") String endDate) throws IOException, ParseException {

        Long rowsSaved = api.initialize(startDate, endDate);

        return new ResponseEntity<>(rowsSaved, HttpStatus.OK);
    }

    @PostMapping("/forecaster/data/update")
    public ResponseEntity<Long> update(@RequestParam(name="end_date", required=true) String endDate) throws IOException, ParseException {

        Long rowsSaved = api.update(endDate);

        return new ResponseEntity<>(rowsSaved, HttpStatus.OK);
    }

    // Получение статуса по загруженным данным, какие даты и страны в наличии
    @GetMapping("/forecaster/data/status")
    public ResponseEntity<ObjectNode> status() {
        ObjectNode status = ucdpEventService.getStatus();
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @GetMapping("/forecaster/predict")
    public ResponseEntity<ObjectNode> predict(@RequestParam(name="country_id", required=true) int countryId,
                          @RequestParam(name="violence_type", required=true) int violenceType,
                          @RequestParam(name="timespan", required=true) int timespan) {

        ObjectNode prediction = predictionService.predict(countryId,violenceType,timespan);
        return new ResponseEntity<>(prediction, HttpStatus.OK);
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
