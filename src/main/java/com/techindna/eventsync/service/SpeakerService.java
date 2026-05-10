package com.techindna.eventsync.service;

import com.techindna.eventsync.dto.PaginationRequestDto;
import com.techindna.eventsync.dto.SpeakerRequestDto;
import com.techindna.eventsync.dto.SpeakerResponseDto;
import com.techindna.eventsync.dto.UpdateSpeakerResponseDto;
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

/*
    public SpeakerResponseDto createSpeaker(SpeakerRequestDto speakerRequestDto){

        dataValidator.validateSpeakerData(
                speakerRequestDto.getFirstName(),
                speakerRequestDto.getLastName(),
                speakerRequestDto.getEmail(),
                speakerRequestDto.getBio(),
                speakerRequestDto.getProfilePicture()
        );
        dataValidator.validateExternalLinks(speakerRequestDto.getExternalLinks());

        SpeakerResponseDto speaker = speakerRepository.createSpeaker(speakerRequestDto);
        if (speaker == null){
            throw new ConflictException(String.format("Speaker with email %s already exists", speakerRequestDto.getEmail()));
        }
        return speaker;
    }
*/

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
