package com.conflict.forecaster.service;

import com.conflict.forecaster.database.entity.UCDPEvent;
import com.conflict.forecaster.database.UCDPEventCountRepository;
import com.conflict.forecaster.database.UCDPEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UCDPEventService {

    @Autowired
    private UCDPEventCountRepository ucdpEventCountRepository;
    @Autowired
    private UCDPEventRepository ucdpEventRepository;



}
