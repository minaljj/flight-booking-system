package com.flightapp.flight_service.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
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
import com.flightapp.flight_service.model.Airline;
import com.flightapp.flight_service.service.AirlineService;

class AirlineControllerTest {
	MockMvc mockMvc;
	@Mock
	AirlineService airlineService;
	@InjectMocks
	AirlineController airlineController;
	ObjectMapper objectMapper=new ObjectMapper();
	Airline airline = new Airline();
	
	@BeforeEach
	void setUp()
	{
		MockitoAnnotations.openMocks(this);
		mockMvc=MockMvcBuilders.standaloneSetup(airlineController).build();		
        airline.setId(1L);
        airline.setName("Indigo");
        airline.setContactNumber("9877894563");
        airline.setContactAddress("Mumbai");
	}
	
	@Test
	void testRegisterAirline() throws Exception
	{
		when(airlineService.registerAirline(airline)).thenReturn(airline);
		mockMvc.perform(post("/api/v1.0/flight/airline")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(airline)))
				.andExpect(status().isCreated());
		
	}
	@Test
	void testGetAllAirlines() throws Exception
	{
		List<Airline>  airlines=new ArrayList<>();
		airlines.add(airline);
		when(airlineService.getAllAirlines()).thenReturn(airlines);
		mockMvc.perform(get("/api/v1.0/flight/airline/list"))
			.andExpect(status().isOk());
	}
	@Test
	void testBlockAirline() throws Exception {

	    mockMvc.perform(delete("/api/v1.0/flight/airline/block/Indigo"))
	            .andExpect(status().isOk());
	}
	@Test
	void testBlockAirlineNotFound() throws Exception {

	    org.mockito.Mockito.doThrow(new RuntimeException("Airline not found"))
	            .when(airlineService)
	            .blockAirline("Indigo");

	    mockMvc.perform(delete("/api/v1.0/flight/airline/block/Indigo"))
	            .andExpect(status().isBadRequest());
	}
	


}
