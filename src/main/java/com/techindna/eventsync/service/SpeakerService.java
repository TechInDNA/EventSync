package com.techindna.eventsync.service;

import com.techindna.eventsync.dto.ExternalLinkDto;
import com.techindna.eventsync.dto.PaginationRequestDto;
import com.techindna.eventsync.dto.SpeakerResponseDto;
import com.techindna.eventsync.exception.ConflictException;
import com.techindna.eventsync.repository.SpeakerRepository;
import com.techindna.eventsync.validator.ExternalLinksValidator;
import com.techindna.eventsync.validator.StringValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SpeakerService {
    private final SpeakerRepository speakerRepository;
    private final StringValidator stringValidator;
    private final ExternalLinksValidator externalLinksValidator;

    public SpeakerService(SpeakerRepository speakerRepository, StringValidator stringValidator, ExternalLinksValidator externalLinksValidator){
        this.speakerRepository = speakerRepository;
        this.stringValidator = stringValidator;
        this.externalLinksValidator = externalLinksValidator;
    }

    public List<SpeakerResponseDto> getAllSpeakers(PaginationRequestDto paginationRequestDto){
        return speakerRepository.getAllSpeakers(paginationRequestDto.getOffset(), paginationRequestDto.getLimit());
    }

    public int countSpeaker(){
        return speakerRepository.countSpeakers();
    }

    @Transactional
    public SpeakerResponseDto createSpeaker(String firstName, String lastName, String email,
                                            String profilePicture, String bio,
                                            List<ExternalLinkDto> externalLinks){
        
        if (profilePicture != null && !profilePicture.isEmpty()){
            stringValidator.validateUrl(profilePicture);
        }

        externalLinksValidator.validateExternalLinks(externalLinks);

        SpeakerResponseDto speaker = speakerRepository.createSpeaker(firstName, lastName, email, profilePicture, bio, externalLinks);
        if (speaker == null){
            throw new ConflictException(String.format("Speaker with email %s already exists", email));
        }
        return speaker;
    }
}
