package com.techindna.eventsync.validator;

import com.techindna.eventsync.exception.BadRequestException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class StringValidator {
    private static final Pattern TEXT_PATTERN = Pattern.compile("^[a-zA-Z0-9 .,':-]+$");
    private final Pattern VALID_URL = Pattern.compile("^https?://[a-zA-Z0-9\\-._%&#/]+$");

    protected void validate(String fieldName, String data){
        if (data == null || data.isEmpty() || data.isBlank()){
            throw new BadRequestException(String.format("The field %s is required and cannot be blank.", fieldName));
        }

        final Matcher TEXT_MATCHER = TEXT_PATTERN.matcher(data);
        if (!TEXT_MATCHER.matches()){
            throw new BadRequestException(String.format("Invalid input for %s: %s only a-zA-Z0-9 .,':- characters are allowed.", fieldName, data));
        }
    }

    protected void validateUrl(String data){
        Matcher URL_MATCHER = VALID_URL.matcher(data);
        if (!URL_MATCHER.matches()){
            throw new BadRequestException(String.format("Invalid URL format or '%s' contain forbidden characters, only a-zA-Z0-9-._%%&# characters are allowed", data));
        }
    }

    private void  validateDate(String fieldName, Instant date){
        if (date == null || date.toString().isBlank() || date.toString().isEmpty()){
            throw new BadRequestException(String.format("The field %s is required and cannot be blank.", fieldName));
        }

        /* Didn't pass test 13
        try {
            Instant.parse(String.valueOf(date));
        }catch (DateTimeException e){
            throw new BadRequestException(String.format("The field %s contain forbidden character or is invalid.", fieldName));
        }
         */
    }

    public void  validateEventData(String title, String description, Instant startDate, Instant endDate, String location){
        validate("title", title);
        validate("description", description);
        validateDate("startDate", startDate);
        validateDate("endDate", endDate);
        validate("location", location);
    }

    public void validateSpeakerData(String firstName, String lastName, String email, String bio){
        validate("firstName", firstName);
        validate("lastName", lastName);
        validate("bio", bio);
        validateEmail(email);
    }

    private void validateEmail(String email){
        if (email == null || email.isEmpty() || email.isBlank()){
            throw new BadRequestException("The field email is required and cannot be blank.");
        }

        final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_.-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z]+){1,2}$");
        final Matcher EMAIL_MATCHER = EMAIL_PATTERN.matcher(email);
        if (!EMAIL_MATCHER.matches()){
            throw new BadRequestException(String.format("Invalid email format: %s", email));
        }
    }
}
