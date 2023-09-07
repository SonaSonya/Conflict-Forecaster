package com.conflict.forecaster.models;

import com.conflict.forecaster.repo.UCDPEventCountRepository;
import com.conflict.forecaster.repo.UCDPEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

@Service
public class UCDPEventCounter {

    @Autowired
    private UCDPEventCountRepository ucdpEventCountRepository;
    @Autowired
    private UCDPEventRepository ucdpEventRepository;



}
