package com.flightapp.user_service.dto;

import com.flightapp.user_service.config.PasswordProperties;
import com.flightapp.user_service.validation.PasswordValidator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DtoValidationTest {

	private Validator validator;

	@BeforeEach
	public void setUp() {
		LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean();
		PasswordProperties properties = new PasswordProperties();
		properties.setPasswordRegex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$");
		properties.setPasswordMessage("Password must be strong");

		factoryBean.setConstraintValidatorFactory(new ConstraintValidatorFactory() {
			@Override
			public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
				try {
					T instance = key.getDeclaredConstructor().newInstance();
					if (instance instanceof PasswordValidator) {
						ReflectionTestUtils.setField(instance, "passwordProperties", properties);
					}
					return instance;
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public void releaseInstance(ConstraintValidator<?, ?> instance) {
			}
		});
		factoryBean.afterPropertiesSet();
		this.validator = factoryBean.getValidator();
	}

	@Test
	public void testSignupRequestValid() {
		SignupRequest request = new SignupRequest();
		request.setUsername("validuser");
		request.setEmail("valid@test.com");
		request.setPassword("Password@123");

		Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
		assertTrue(violations.isEmpty(), "Expected no violations for valid signup request");
	}

	@Test
	public void testSignupRequestInvalidEmail() {
		SignupRequest request = new SignupRequest();
		request.setUsername("validuser");
		request.setEmail("invalid-email");
		request.setPassword("Password@123");

		Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
		assertFalse(violations.isEmpty(), "Expected violations for invalid email");
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
	}

	@Test
	public void testSignupRequestShortPassword() {
		SignupRequest request = new SignupRequest();
		request.setUsername("validuser");
		request.setEmail("valid@test.com");
		request.setPassword("short");

		Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
		assertFalse(violations.isEmpty(), "Expected violations for short password");
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
	}
	
	@Test
	public void testSignupRequestInvalidPassword() {
		SignupRequest request = new SignupRequest();
		request.setUsername("validuser");
		request.setEmail("valid@test.com");
		request.setPassword("Password");

		Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
		assertFalse(violations.isEmpty(), "Expected violations for short password");
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
		
		request.setPassword("Password123");

		violations = validator.validate(request);
		assertFalse(violations.isEmpty(), "Expected violations for short password");
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
		
		request.setPassword("password@123");

		violations = validator.validate(request);
		assertFalse(violations.isEmpty(), "Expected violations for short password");
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
		
		request.setPassword("            ");

		violations = validator.validate(request);
		assertFalse(violations.isEmpty(), "Expected violations for short password");
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
		
		request.setPassword(null);
		violations = validator.validate(request);
		assertFalse(violations.isEmpty(), "Expected violations for short password");
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
	}

	@Test
	public void testLoginRequestForEmptyUsername() {
		LoginRequest request = new LoginRequest();
		request.setUsername("");
		request.setPassword("Password@123");

		Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
		assertFalse(violations.isEmpty());
		assertTrue(violations.size() == 1);
	}
	
	@Test
	public void testLoginRequestForEmptyPassword() {
		LoginRequest request = new LoginRequest();
		request.setUsername("testuser");
		request.setPassword("");

		Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
		assertFalse(violations.isEmpty());
		assertTrue(violations.size() == 1);
	}
	
	@Test
	public void testLoginRequestForBlankUsername() {
		LoginRequest request = new LoginRequest();
		request.setUsername("         ");
		request.setPassword("Password@123");

		Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
		assertFalse(violations.isEmpty());
		assertTrue(violations.size() == 1);
	}
	
	@Test
	public void testLoginRequestForBlankPassword() {
		LoginRequest request = new LoginRequest();
		request.setUsername("testuser");
		request.setPassword("        ");

		Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
		assertFalse(violations.isEmpty());
		assertTrue(violations.size() == 1);
	}

}
