package com.flightapp.flight_service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flightapp.flight_service.model.Airline;
import com.flightapp.flight_service.repository.AirlineRepository;

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


    public void blockAirline(String name) {
        Optional<Airline> airlineOpt = airlineRepository.findByName(name);
        if (airlineOpt.isEmpty()) {
            throw new RuntimeException("Airline not found");
        }
        Airline airline = airlineOpt.get();
        airline.setBlocked(true);
        airlineRepository.save(airline);
    }


	public List<Airline> getAllAirlines()
	{
		return airlineRepository.findAll();
	}
	
}