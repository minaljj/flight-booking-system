package com.flightapp.booking_service.integration.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.flightapp.booking_service.dto.NotificationRequest;
import com.flightapp.booking_service.integration.NotificationGateway;

@Service
@Profile("integration")
public class RealNotificationGateway implements NotificationGateway {

	@Autowired
	private RestTemplate restTemplate;

	@Value("${notification.service.url}")
	private String notificationServiceUrl;

	@Override
	public void sendNotification(NotificationRequest request) {
		try {
			Map<String, Object> payload = new HashMap<>();
			payload.put("type", request.getEventType());
			payload.put("email", request.getEmailId());
			payload.put("phone", request.getPhoneNumber());
			payload.put("pnr", request.getPnr());
			payload.put("seats", request.getNoOfSeats());
			payload.put("flightId", request.getFlightId());

			restTemplate.postForObject(notificationServiceUrl, payload, Map.class);
		} catch (Exception e) {
			System.err.println("Failed to trigger notification: " + e.getMessage());
		}
	}
}
