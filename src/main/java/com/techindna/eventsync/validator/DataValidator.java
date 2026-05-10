package com.techindna.eventsync.validator;

import com.techindna.eventsync.dto.ExternalLinkDto;
import com.techindna.eventsync.dto.SpeakerRequestDto;
import com.techindna.eventsync.exception.BadRequestException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class DataValidator {
    private static final Pattern TEXT_PATTERN = Pattern.compile("^[a-zA-Z0-9 .,':-]+$");
    private final Pattern VALID_URL = Pattern.compile("^https?://[a-zA-Z0-9\\-._%&#/]+$");
    private static final Pattern VALID_INTEGER = Pattern.compile("^[1-9][0-9]*$");
    private static final Pattern UUID_PATTERN = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
    private static final Pattern VALID_DATE = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z$");
    private static final Pattern VALID_EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_.-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z]+){1,2}$");

    protected void lengthValidation(String fieldName, int limit, String data){
        if (data != null && data.length() > limit){
            throw new BadRequestException(String.format("The length of %s field cannot exceed %d.", fieldName, limit));
        }
    }

    public void validatePageAndSize(String page, String size){

        final Matcher PAGE = VALID_INTEGER.matcher(page);
        final Matcher SIZE = VALID_INTEGER.matcher(size);

        if (!PAGE.matches() || !SIZE.matches()){
            throw new BadRequestException("The page and the size parameter must be a digit greater than 0.");
        }
    }

    public void checkNullData(String fieldName, String data){
        if (data == null || data.isEmpty() || data.isBlank()){
            throw new BadRequestException(String.format("The field %s is required and cannot be blank.", fieldName));
        }
    }

    protected void validateString(String fieldName, String data){
        checkNullData(fieldName, data);
        final Matcher TEXT_MATCHER = TEXT_PATTERN.matcher(data);

        if (!TEXT_MATCHER.matches()){
            throw new BadRequestException(String.format("Invalid input for %s: %s only a-zA-Z0-9 .,':- characters are allowed.", fieldName, data));
        }
    }

    public void validateUrl(String data){
        Matcher URL_MATCHER = VALID_URL.matcher(data);
        if (!URL_MATCHER.matches()){
            throw new BadRequestException(String.format("Invalid URL format or '%s' contain forbidden characters, only a-zA-Z0-9-._%%&# characters are allowed", data));
        }
    }

    private void  validateDate(String fieldName, String date){
        checkNullData(fieldName, date);
        final Matcher DATE_MATCHER = VALID_DATE.matcher(date);

        if (!DATE_MATCHER.matches()){
            throw new BadRequestException(String.format("Invalid Date format or %s field contain forbidden characters.", fieldName));
        }

    }

    public void  validateEventData(String title, String description, String startDate, String endDate, String location){
        lengthValidation("title", 50, title);
        validateString("title", title);
        validateString("description", description);
        validateDate("startDate", startDate);
        validateDate("endDate", endDate);
        lengthValidation("location", 50, location);
        validateString("location", location);
    }

    public void validateSpeakerData(SpeakerRequestDto speakerRequestDto){
        lengthValidation("firstName", 50, speakerRequestDto.getFirstName());
        validateString("firstName", speakerRequestDto.getFirstName());
        lengthValidation("lastName", 50, speakerRequestDto.getLastName());
        validateString("lastName", speakerRequestDto.getLastName());
        validateString("bio", speakerRequestDto.getBio());
        validateEmail(speakerRequestDto.getEmail());
    }

    public void validateRoomData(String name){
        checkNullData("name", name);
        lengthValidation("name", 50, name);
        validateString("name", name);
    }

    public void validateUUID(String uuid){
        if (uuid == null || uuid.isEmpty()){
            throw new BadRequestException("UUID path variable cannot be null or blank.");
        }

        final Matcher UUID_MATCHER = UUID_PATTERN.matcher(uuid);
        if (!UUID_MATCHER.matches()){
            throw new BadRequestException("Invalid UUID format.");
        }
    }

    public void validateEmail(String email){
        checkNullData("email", email);
        lengthValidation("email", 50, email);
        final Pattern ALLOWED_CHAR = Pattern.compile("^[a-zA-Z0-9.@_-]+$");
        final Matcher EMAIL_MATCHER = VALID_EMAIL_PATTERN.matcher(email);

        if (!ALLOWED_CHAR.matcher(email).matches()){
            throw new BadRequestException(String.format("Invalid input for email: '%s' only a-zA-Z0-9@_.- characters are allowed.", email));
        }

        if (!EMAIL_MATCHER.matches()){
            throw new BadRequestException(String.format("Invalid email format: '%s'", email));
        }
    }

    public void validateExternalLinks(List<ExternalLinkDto> links){
        if (links != null){
            for (ExternalLinkDto l : links){
                validateString("name", l.getName());
                validateUrl(l.getUrl());
            }
        }
    }
}
