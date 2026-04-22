package com.flightapp.user_service.validation;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;

import jakarta.validation.ConstraintValidator;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {
	  @Autowired
	    private PasswordProperties passwordProperties;

	    @Override
	    public boolean isValid(String password, ConstraintValidatorContext context) {
	        if (password == null) {
	            return false;
	        }

	        String regex = passwordProperties.getPasswordRegex();
	        String message = passwordProperties.getPasswordMessage();

	        if (!Pattern.matches(regex, password)) {
	            context.disableDefaultConstraintViolation();
	            context.buildConstraintViolationWithTemplate(message)
	                   .addConstraintViolation();
	            return false;
	        }

	        return true;
	    }
}
