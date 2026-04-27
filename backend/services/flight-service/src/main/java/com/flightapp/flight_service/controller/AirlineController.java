package com.flightapp.flight_service.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flightapp.flight_service.model.Airline;
import com.flightapp.flight_service.service.AirlineService;

import jakarta.validation.Valid;
@RestController
@RequestMapping("/api/v1.0/flight")
public class AirlineController {
    @Autowired
    private AirlineService airlineService;
    public static final String ADMIN = "hasRole('ADMIN')";
   
    @PostMapping("/airline")
    @PreAuthorize(ADMIN)
    public ResponseEntity<Long> registerAirline(@Valid @RequestBody Airline airline) {
        airlineService.registerAirline(airline);
		return ResponseEntity.status(201).body(airline.getId());
    }

    @DeleteMapping("/airline/block/{name}")
    @PreAuthorize(ADMIN)
    public ResponseEntity<?> blockAirline(@PathVariable String name) {
        try {
            airlineService.blockAirline(name);
            return ResponseEntity.ok("Airline blocked successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

