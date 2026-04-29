package com.flightapp.flight_service.controller;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.flightapp.flight_service.dto.FlightSearchRequest;
import com.flightapp.flight_service.model.Flight;
import com.flightapp.flight_service.model.MealType;
import com.flightapp.flight_service.service.FlightService;

class FlightControllerTest {

    MockMvc mockMvc;

    @Mock
    FlightService flightService;

    @InjectMocks
    FlightController flightController;

    ObjectMapper objectMapper = new ObjectMapper();

    Flight flight;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders
                .standaloneSetup(flightController)
                .build();

        flight = new Flight();
        flight.setId(1L);
        flight.setFlightNumber("6E101");
        flight.setAirline("Indigo");
        flight.setFrom("Bangalore");
        flight.setTo("Pune");
        flight.setStartDateTime(LocalDateTime.of(2026, 5, 1, 10, 0));
        flight.setEndDateTime(LocalDateTime.of(2026, 5, 1, 12, 0));
        flight.setScheduledDays("Monday");
        flight.setInstrumentUsed("Airbus A320");
        flight.setTotalBusinessSeats(20);
        flight.setTotalNonBusinessSeats(80);
		flight.setMeal(MealType.VEG);
        flight.setNumberOfRows(10);
        flight.setTicketCost(5000.0);
        flight.setIsBlocked(false);
    }
    @Test
    void testAddInventory() throws Exception {

        when(flightService.addInventory(any(Flight.class))).thenReturn(flight);

        mockMvc.perform(post("/api/v1.0/flight/airline/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(flight)))
                .andExpect(status().isCreated());
    }
    @Test
    void testSearchFlights() throws Exception
    {
    	FlightSearchRequest request =new FlightSearchRequest();
    	request.setFrom("Bangalore");
    	request.setTo("Hyderabad");
    	request.setDate("2026-07-28");
    	when(flightService.searchFlights(any(FlightSearchRequest.class))).thenReturn(List.of(flight));
    }
    @Test
    void testGetFlight() throws Exception
    {
    	when(flightService.getFlightById(1L)).thenReturn(flight);
    	mockMvc.perform(get("/api/v1.0/flight/1")).andExpect(status().isOk());
    	
    }
    @Test
    void testGetFlightNotFound()throws Exception
    {
    	when(flightService.getFlightById(2L)).thenReturn(null);
    	mockMvc.perform(get("/api/v1.0/flight/2")).andExpect(status().isNotFound());
    }
    
  
   
    

}