package com.techindna.eventsync.service;

import com.techindna.eventsync.dto.*;
import com.techindna.eventsync.exception.ConflictException;
import com.techindna.eventsync.exception.NotFoundException;
import com.techindna.eventsync.repository.SpeakerRepository;
import com.techindna.eventsync.validator.DataValidator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SpeakerService {
    private final SpeakerRepository speakerRepository;
    private final DataValidator dataValidator;

    public SpeakerService(SpeakerRepository speakerRepository, DataValidator dataValidator){
        this.speakerRepository = speakerRepository;
        this.dataValidator = dataValidator;
    }

    public List<SpeakerResponseDto> getAllSpeakers(PaginationRequestDto paginationRequestDto){
        return speakerRepository.getAllSpeakers(paginationRequestDto.getOffset(), paginationRequestDto.getLimit());
    }

    public int countSpeaker(){
        return speakerRepository.countSpeakers();
    }


    public SpeakerResponseDto createSpeaker(SpeakerRequestDto speakerRequest, List<ExternalLinkDto> externalLinks){

        dataValidator.validateSpeakerData(speakerRequest);
        dataValidator.validateExternalLinks(externalLinks);

        return speakerRepository.createSpeaker(speakerRequest, externalLinks);
    }


    public UpdateSpeakerResponseDto updateSpeakerById(UUID id, SpeakerRequestDto request) {
        dataValidator.validateSpeakerData(request);
        return speakerRepository.updateSpeakerById(id, request);
    }

    public void deleteSpeaker(UUID id) {
        boolean deleted = speakerRepository.deleteSpeaker(id);

        if (!deleted) {
            throw new NotFoundException("Speaker not found with ID: " + id);
        }
    }

}
