package com.juli.services;

import com.juli.models.Statistics;
import org.springframework.http.HttpStatus;

/**
 * LastMinuteService is a service used to store, retrieve and delete data of the last 60 seconds
 */
public interface LastMinuteService {
    Statistics getStatistics();
    HttpStatus insert(String data);
    HttpStatus deleteAllData();
}
