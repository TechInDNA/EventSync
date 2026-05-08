package com.techindna.eventsync.validator;

import com.techindna.eventsync.dto.ExternalLinkDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExternalLinksValidator {
    private final DataValidator dataValidator;
    public ExternalLinksValidator(DataValidator dataValidator){
        this.dataValidator = dataValidator;
    }

    public void validateExternalLinks(List<ExternalLinkDto> links){
        if (links != null){
            for (ExternalLinkDto l : links){
                dataValidator.validateString("name", l.getName());
                dataValidator.validateUrl(l.getUrl());
            }
        }
    }
}
