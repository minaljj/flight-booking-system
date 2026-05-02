package com.flightapp.flight_service.kafka;

import com.flightapp.flight_service.dto.SeatUpdateRequest;
import com.flightapp.flight_service.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.kafka.support.KafkaHeaders;

@Service
public class FlightEventConsumer {

    @Autowired
    private FlightService flightService;

    @KafkaListener(topics = "flight-seat-update", groupId = "flight-app-group")
    public void handleSeatUpdate(@Payload SeatUpdateRequest request, 
                                 @Header(KafkaHeaders.RECEIVED_KEY) String flightId) {
        System.out.println("Received seat update for flight " + flightId + ": B=" + request.getBusinessDelta() + ", NB=" + request.getNonBusinessDelta());
        flightService.updateSeats(Long.parseLong(flightId), request.getBusinessDelta(), request.getNonBusinessDelta());
    }
}
