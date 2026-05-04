package com.flightapp.flight_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="airlines")
@Data
@NoArgsConstructor
public class Airline {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	@NotBlank(message="Airline name is required")
	private String name;
	private String logo;
	@NotBlank(message="Contact Number is Required")
	private String contactNumber;
	@NotBlank(message="Contact Address is Required")
	private String contactAddress;
	private boolean blocked;
	
	


}
