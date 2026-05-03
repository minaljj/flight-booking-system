package com.flightapp.booking_service.integration.impl;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.flightapp.booking_service.dto.NotificationRequest;
import com.flightapp.booking_service.integration.NotificationGateway;

@Service
@Profile("local")
public class MockNotificationGateway implements NotificationGateway {

	@Override
	public void sendNotification(NotificationRequest request) {

		System.out.println("==== MOCK NOTIFICATION ====");
		System.out.println("Event     : " + request.getEventType());
		System.out.println("PNR       : " + request.getPnr());
		System.out.println("Email     : " + request.getEmailId());
		System.out.println("Phone     : " + request.getPhoneNumber());
		System.out.println("Flight ID : " + request.getFlightId());
		System.out.println("Seats     : " + request.getNoOfSeats());
		System.out.println("============================");
	}
}