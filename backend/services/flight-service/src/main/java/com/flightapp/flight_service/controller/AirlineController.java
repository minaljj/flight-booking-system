package com.flightapp.flight_service.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.flightapp.flight_service.model.Airline;
import com.flightapp.flight_service.service.AirlineService;

import jakarta.validation.Valid;
@RestController
@RequestMapping("/api/v1.0/")
public class AirlineController {
    @Autowired
    private AirlineService airlineService;
    @PostMapping("/airline")
    public ResponseEntity<Long> registerAirline(@Valid @RequestBody Airline airline) {
        airlineService.registerAirline(airline);
		return ResponseEntity.status(201).body(airline.getId());
    }
    
}
