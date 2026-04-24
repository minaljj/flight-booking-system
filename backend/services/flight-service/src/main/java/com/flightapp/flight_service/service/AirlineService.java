package com.flightapp.flight_service.service;

import com.flightapp.flight_service.model.Airline;
import com.flightapp.flight_service.repository.AirlineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AirlineService {

    @Autowired
    private AirlineRepository airlineRepository;

    public Airline registerAirline(Airline airline) {

        if (airlineRepository.findByName(airline.getName()).isPresent()) {
            throw new RuntimeException("Airline already registered!");
        }

        airline.setBlocked(false);
        return airlineRepository.save(airline);
    }

    public Airline blockAirline(String name) {

        Airline airline = airlineRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Airline not found"));

        airline.setBlocked(true);
        return airlineRepository.save(airline);
    }
}