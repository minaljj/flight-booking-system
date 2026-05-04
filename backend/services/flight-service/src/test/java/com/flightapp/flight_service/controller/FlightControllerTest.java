package com.flightapp.flight_service.controller;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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
                .setMessageConverters(
                        new org.springframework.http.converter.json.MappingJackson2HttpMessageConverter(objectMapper))
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
        ResponseEntity<Long> response = flightController.addInventory(flight);
        assertEquals(201, response.getStatusCode().value());
        assertEquals(1L, response.getBody());
    }
    @Test
    void testSearchFlights() throws Exception
    {
    	FlightSearchRequest request =new FlightSearchRequest();
    	request.setFrom("Bangalore");
    	request.setTo("Hyderabad");
    	request.setDate("2026-07-28");
    	Page<Flight> flightPage=new PageImpl<>(List.of(flight));
    	when(flightService.searchFlights(any(FlightSearchRequest.class),any(Pageable.class))).thenReturn(flightPage);
    	ResponseEntity<Page<Flight>> response = flightController.searchFlights(request, 0, 10);
    	 assertEquals(200, response.getStatusCode().value());
    	    assertNotNull(response.getBody());
    	    assertEquals(1, response.getBody().getContent().size());
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
    @Test
    void testGetAllFlights() throws Exception {
        Page<Flight> flightPage = new PageImpl<>(List.of(flight));

        when(flightService.getAllFlights(any(Pageable.class))).thenReturn(flightPage);

        ResponseEntity<Page<Flight>> response = flightController.getAllFlights(0, 10);

        assertEquals(200, response.getStatusCode().value());
    }
    
  
   
    

}