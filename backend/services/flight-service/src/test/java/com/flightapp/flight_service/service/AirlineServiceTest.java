package com.flightapp.flight_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.flightapp.flight_service.model.Airline;
import com.flightapp.flight_service.repository.AirlineRepository;

@ExtendWith(MockitoExtension.class)
class AirlineServiceTest {

    @Mock
    private AirlineRepository airlineRepository;

    @InjectMocks
    private AirlineService airlineService;

    private Airline airline;

    @BeforeEach
    void setUp() {
        airline = new Airline();
        airline.setName("Vistara");
        airline.setLogo("vistara.png");
        airline.setContactNumber("9876543210");
        airline.setContactAddress("Bangalore Airport Road");
    }

    
    @Test
    void registerAirline_shouldSaveAirline_whenValidAirline() {
        when(airlineRepository.findByName("Vistara"))
                .thenReturn(Optional.empty());

        when(airlineRepository.save(Mockito.any(Airline.class)))
                .thenReturn(airline);

        
        Airline savedAirline = airlineService.registerAirline(airline);

        assertNotNull(savedAirline);
        assertEquals("Vistara", savedAirline.getName());
        assertFalse(savedAirline.isBlocked());

        verify(airlineRepository, times(1)).findByName("Vistara");
        verify(airlineRepository, times(1)).save(airline);
    }
    @Test
    void registerAirline_shouldThrowException_whenAirlineAlreadyExists() {
        when(airlineRepository.findByName("Vistara"))
                .thenReturn(Optional.of(airline));

        assertThrows(RuntimeException.class, () -> {
            airlineService.registerAirline(airline);
        });

        verify(airlineRepository, times(1)).findByName("Vistara");
        verify(airlineRepository, never()).save(Mockito.any(Airline.class));
    }
  }