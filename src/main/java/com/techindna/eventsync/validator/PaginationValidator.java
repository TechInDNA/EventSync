package com.techindna.eventsync.validator;

import com.techindna.eventsync.exception.BadRequestException;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PaginationValidator {
    private static final Pattern VALID_INPUT = Pattern.compile("^[1-9]+$");

    public void validatePageAndSize(String page, String size){
        if (page == null || page.isEmpty() || size == null || size.isEmpty()) {
            throw new BadRequestException("Page and size parameters are required and cannot be null or empty.");
        }

        final Matcher PAGE = VALID_INPUT.matcher(page);
        final Matcher SIZE = VALID_INPUT.matcher(size);

        if (!PAGE.matches() || !SIZE.matches()){
            throw new BadRequestException("The page and the size parameter must be a digit character greater than 0.");
        }
    }
}
