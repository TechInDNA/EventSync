package com.techindna.eventsync.service;

import com.techindna.eventsync.dto.*;
import com.techindna.eventsync.dto.speaker.SpeakerRequestDto;
import com.techindna.eventsync.dto.speaker.SpeakerResponseDto;
import com.techindna.eventsync.dto.speaker.UpdateSpeakerResponseDto;
import com.techindna.eventsync.exception.ConflictException;
import com.techindna.eventsync.exception.NotFoundException;
import com.techindna.eventsync.repository.SpeakerRepository;
import com.techindna.eventsync.validator.DataValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


    @Transactional
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

    public List<ExternalLinkDto> addExternalLink(String id, ExternalLinkDto link) {
        dataValidator.validateUUID(id);
        dataValidator.validateExternalLink(link);

        UUID speakerId = UUID.fromString(id);

        if (!speakerRepository.speakerExists(speakerId)) {
            throw new NotFoundException(String.format("Speaker ID %s not found.", id));
        }

        List<ExternalLinkDto> existingLinks = speakerRepository.getSpeakerExternalLinks(speakerId);

        boolean nameConflict = existingLinks.stream()
                .anyMatch(existing -> existing.getName().equals(link.getName()));
        if (nameConflict) {
            throw new ConflictException(
                    String.format("A link with name '%s' already exists for this speaker.", link.getName())
            );
        }

        boolean urlConflict = existingLinks.stream()
                .anyMatch(existing -> existing.getUrl().equals(link.getUrl()));
        if (urlConflict) {
            throw new ConflictException(
                    String.format("A link with URL '%s' already exists for this speaker.", link.getUrl())
            );
        }

        return speakerRepository.insertExternalLink(speakerId, link);
    }

}
