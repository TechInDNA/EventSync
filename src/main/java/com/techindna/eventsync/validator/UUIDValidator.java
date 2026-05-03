package com.techindna.eventsync.validator;

import com.techindna.eventsync.exception.BadRequestException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UUIDValidator {
    private static final Pattern UUID_PATTERN = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    public void validateUUID(String uuid){
        final Matcher UUID_MATCHER = UUID_PATTERN.matcher(uuid);

        if (!UUID_MATCHER.matches()){
            throw new BadRequestException("Invalid UUID format.");
        }
    }
}
