package com.conflict.forecaster.service;

import com.conflict.forecaster.database.entity.UCDPEvent;
import com.conflict.forecaster.database.UCDPEventCountRepository;
import com.conflict.forecaster.database.UCDPEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UCDPEventService {

    @Autowired
    private UCDPEventCountRepository ucdpEventCountRepository;
    @Autowired
    private UCDPEventRepository ucdpEventRepository;

    public ObjectNode getStatus () {
        UCDPEvent earliestEvent = ucdpEventRepository.findFirst1ByOrderByYearDescMonthDesc().get(0);
        UCDPEvent latestEvent = ucdpEventRepository.findFirst1ByOrderByYearAscMonthAsc().get(0);
        List<Object[]> countries = ucdpEventRepository.findCountries();

        ObjectMapper mapper = new ObjectMapper();
        ArrayNode countriesNode = mapper.createArrayNode();
        ObjectNode status = mapper.createObjectNode();

        for (Object[] country : countries)
        {
            ObjectNode countryNode = mapper.createObjectNode();
            countryNode.put("id", country[0].toString());
            countryNode.put("name", country[1].toString());

            countriesNode.add(countryNode);
        }

        status.put("start_date", earliestEvent.getMonth() + ".0." + earliestEvent.getYear());
        status.put("end_date", latestEvent.getMonth() + ".0." + latestEvent.getYear());
        status.set("countries", countriesNode);

        return status;
    }


}
