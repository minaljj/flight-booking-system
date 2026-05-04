package com.flightapp.booking_service.integration;

import com.flightapp.booking_service.dto.NotificationRequest;

public interface NotificationGateway {
	void sendNotification(NotificationRequest request);
}
