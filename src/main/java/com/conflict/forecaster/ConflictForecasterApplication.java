package com.conflict.forecaster;

import com.conflict.forecaster.models.UCDPApiClient;
import com.conflict.forecaster.repo.UCDPEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ConflictForecasterApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConflictForecasterApplication.class, args);
	}

}
