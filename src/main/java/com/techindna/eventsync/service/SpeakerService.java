package com.techindna.eventsync.service;

import com.techindna.eventsync.dto.*;
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


    public SpeakerResponseDto createSpeaker(PostSpeakersRequestDto postSpeakersRequestDto){

        dataValidator.validateSpeakerData(postSpeakersRequestDto);
        dataValidator.validateExternalLinks(postSpeakersRequestDto.getExternalLinks());

        return speakerRepository.createSpeaker(postSpeakersRequestDto, postSpeakersRequestDto.getExternalLinks());
    }


    public UpdateSpeakerResponseDto updateSpeakerById(UUID id, SpeakerRequestDto request) {
        dataValidator.validateSpeakerData(request);
        return speakerRepository.updateSpeakerById(id, request);
    }

    public void deleteSpeaker(UUID id) {
        UUID deleted = speakerRepository.deleteSpeakerById(id);

        if (deleted == null) {
            throw new NotFoundException(String.format("Speaker ID %s not found.", id));
        }
    }

}
