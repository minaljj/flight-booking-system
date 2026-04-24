package com.flightapp.flight_service.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flightapp.flight_service.dto.FlightSearchRequest;
import com.flightapp.flight_service.model.Flight;
import com.flightapp.flight_service.service.FlightService;

import jakarta.validation.Valid;
/*
 * author:Preethi Anna Baby**/

@RestController
@RequestMapping("/api/v1.0/flight")
public class FlightController {
    @Autowired
    private FlightService flightService;
    @PostMapping("/airline/inventory")

    public ResponseEntity<Long> addInventory(@Valid @RequestBody Flight flight) {
        Flight savedFlight = flightService.addInventory(flight);
        return ResponseEntity.status(201).body(savedFlight.getId());   
    }
    @PostMapping("/search")
    public ResponseEntity<List<Flight>> searchFlights(@Valid @RequestBody FlightSearchRequest request) {
        List<Flight> flights = flightService.searchFlights(request);
        return ResponseEntity.ok(flights);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Flight> getFlight(@PathVariable Long id) {
        Flight foundFlight = flightService.getFlightById(id);

        if (foundFlight == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(foundFlight);
    }
}