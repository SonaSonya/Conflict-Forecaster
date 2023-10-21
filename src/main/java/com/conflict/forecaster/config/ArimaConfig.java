package com.conflict.forecaster.config;

import com.workday.insights.timeseries.arima.struct.ArimaParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ArimaConfig {

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

    @Bean
    public ArimaParams arimaConf() {
        return new ArimaParams(this.p, this.d, this.q, this.P, this.D, this.Q, this.m);
    }

}
