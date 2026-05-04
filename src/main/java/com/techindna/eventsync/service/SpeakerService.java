package com.techindna.eventsync.service;

import com.techindna.eventsync.dto.ExternalLinkDto;
import com.techindna.eventsync.dto.PaginationRequestDto;
import com.techindna.eventsync.dto.SpeakerRequestDto;
import com.techindna.eventsync.dto.SpeakerResponseDto;
import com.techindna.eventsync.exception.ConflictException;
import com.techindna.eventsync.exception.NotFoundException;
import com.techindna.eventsync.repository.SpeakerRepository;
import com.techindna.eventsync.validator.ExternalLinksValidator;
import com.techindna.eventsync.validator.StringValidator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

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


    public void updateSpeaker(UUID id, SpeakerRequestDto request) {

        stringValidator.validateSpeakerData(request.getFirstName(), request.getLastName(), request.getEmail(), request.getBio());

        boolean updated = speakerRepository.updateSpeaker(
                id,
                request.getFirstName(),
                request.getLastName(),
                request.getProfilePicture(),
                request.getBio(),
                request.getExternalLinks()
        );

        if (!updated) {
            throw new NotFoundException("Speaker not found with ID : " + id);
        }
    }

    public void deleteSpeaker(UUID id) {
        boolean deleted = speakerRepository.deleteSpeaker(id);

        if (!deleted) {
            throw new NotFoundException("Speaker not found with ID: " + id);
        }
    }

}
