package com.flightapp.flight_service.controller;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public static final String ROLE = "hasAnyRole('ADMIN', 'AIRLINE_MODERATOR')";
    @Autowired
    private FlightService flightService;
    @PostMapping("/airline/inventory")
    @PreAuthorize(ROLE)
    public ResponseEntity<Long> addInventory(@Valid @RequestBody Flight flight) {
        Flight savedFlight = flightService.addInventory(flight);
        return ResponseEntity.status(201).body(savedFlight.getId());   
    }
    @PostMapping("/search")
    public ResponseEntity<Page<Flight>> searchFlights(
            @Valid @RequestBody FlightSearchRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(flightService.searchFlights(request, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Flight> getFlight(@PathVariable Long id) {
        Flight foundFlight = flightService.getFlightById(id);

        if (foundFlight == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(foundFlight);
    }
    @GetMapping("/all")
    public ResponseEntity<Page<Flight>> getAllFlights(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(flightService.getAllFlights(pageable));
    }
}