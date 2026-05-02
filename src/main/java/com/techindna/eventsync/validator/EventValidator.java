package com.techindna.eventsync.validator;

import com.techindna.eventsync.exception.BadRequestException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.regex.Pattern;

@Component
public class EventValidator {
    private static final Pattern TEXT_PATTERN = Pattern.compile("^[a-zA-Z0-9.,'-]+$");

    private void validate(String data){
        if (data == null || data.isEmpty() || data.isBlank()){
            throw new BadRequestException(String.format("The %s field is required", data));
        }
    }
    public void  validateEventData(String title, String description, Instant startDate, Instant endDate, String location){
        validate(title);
        validate(description);
        validate(String.valueOf(startDate));
        validate(String.valueOf(endDate));
        validate(location);
    }
}
