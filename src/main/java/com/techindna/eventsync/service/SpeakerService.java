package com.techindna.eventsync.service;

import com.techindna.eventsync.dto.ExternalLinkDto;
import com.techindna.eventsync.dto.PaginationRequestDto;
import com.techindna.eventsync.dto.SpeakerResponseDto;
import com.techindna.eventsync.exception.ConflictException;
import com.techindna.eventsync.repository.SpeakerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpeakerService {
    private final SpeakerRepository speakerRepository;

    public SpeakerService(SpeakerRepository speakerRepository){
        this.speakerRepository = speakerRepository;
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
        SpeakerResponseDto speaker = speakerRepository.createSpeaker(firstName, lastName, email, profilePicture, bio, externalLinks);
        if (speaker == null){
            throw new ConflictException(String.format("Speaker with email %s already exists", email));
        }
        return speaker;
    }
}
