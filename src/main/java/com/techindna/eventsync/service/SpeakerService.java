package com.techindna.eventsync.service;

import com.techindna.eventsync.dto.*;
import com.techindna.eventsync.dto.speaker.SpeakerRequestDto;
import com.techindna.eventsync.dto.speaker.UpdateSpeakerResponseDto;
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


    public UpdateSpeakerResponseDto updateSpeakerById(String id, SpeakerRequestDto request) {
        dataValidator.validateUUID(id);
        dataValidator.validateSpeakerData(request);
        return speakerRepository.updateSpeakerById(UUID.fromString(id), request)
                .orElseThrow(() -> new NotFoundException(String.format("Speaker ID %s does not exist.", id)));
    }

    public void deleteSpeaker(String id) {
        dataValidator.validateUUID(String.valueOf(id));
        speakerRepository.deleteSpeakerById(UUID.fromString(id))
                .orElseThrow(() -> new NotFoundException(String.format("Speaker ID %s not found.", id)));
    }

}
