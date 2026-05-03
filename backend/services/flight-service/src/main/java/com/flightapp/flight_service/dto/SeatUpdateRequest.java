package com.flightapp.flight_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeatUpdateRequest {
    private int businessDelta;
    private int nonBusinessDelta;
}
