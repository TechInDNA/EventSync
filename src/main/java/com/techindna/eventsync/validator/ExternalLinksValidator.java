package com.techindna.eventsync.validator;

import com.techindna.eventsync.dto.ExternalLinkDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExternalLinksValidator {
    private final StringValidator stringValidator;
    public ExternalLinksValidator(StringValidator stringValidator){
        this.stringValidator = stringValidator;
    }

    public void validateExternalLinks(List<ExternalLinkDto> links){
        if (links != null){
            for (ExternalLinkDto l : links){
                stringValidator.validate("name", l.getName());
                stringValidator.validateUrl(l.getUrl());
            }
        }
    }
}
