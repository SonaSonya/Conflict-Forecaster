package com.conflict.forecaster.controller;

import com.conflict.forecaster.service.UCDPApiClient;
import com.conflict.forecaster.database.UCDPEventCountRepository;
import com.conflict.forecaster.database.UCDPEventRepository;
import com.conflict.forecaster.service.UCDPEventService;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
    private UCDPEventService ucdpEventService;

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

    // Получение статуса по загруженным данным, какие даты и страны в наличии
    @GetMapping("/forecaster/data/status")
    public ObjectNode status(Model model) {
        return ucdpEventService.getStatus();
    }

    @GetMapping("/forecaster/predict")
    public String predict(@RequestParam(name="country_id", required=true) int countryId,
                          @RequestParam(name="violence_type", required=true) int violenceType,
                          @RequestParam(name="timespan", required=true) int timespan,
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

}
