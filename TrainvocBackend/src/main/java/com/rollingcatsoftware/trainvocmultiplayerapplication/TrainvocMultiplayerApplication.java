package com.rollingcatsoftware.trainvocmultiplayerapplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TrainvocMultiplayerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrainvocMultiplayerApplication.class, args);
    }

}
