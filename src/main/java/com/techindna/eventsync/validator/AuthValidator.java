package com.techindna.eventsync.validator;

import com.techindna.eventsync.exception.BadRequestException;
import org.springframework.stereotype.Component;

@Component
public class AuthValidator {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_.-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z]+){1,2}$";

    public void ValidateData(String email, String password){
        if (email == null || email.isBlank() || email.isEmpty() || password == null || password.isBlank() || password.isEmpty()){
            throw new BadRequestException("Email and password field are required");
        }

        if (!email.matches(EMAIL_REGEX)){
            throw new BadRequestException("Invalid email format");
        }
    }
}
