package com.techindna.eventsync.service;

import com.techindna.eventsync.dto.PaginationRequestDto;
import com.techindna.eventsync.entity.Speaker;
import com.techindna.eventsync.repository.SpeakerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpeakerService {
    private final SpeakerRepository speakerRepository;

    public SpeakerService(SpeakerRepository speakerRepository){
        this.speakerRepository = speakerRepository;
    }

    public List<Speaker> getAllSpeakers(PaginationRequestDto paginationRequestDto){
        return speakerRepository.getAllSpeakers(paginationRequestDto.getOffset(), paginationRequestDto.getLimit());
    }

    public int countSpeaker(){
        return speakerRepository.countSpeakers();
    }
}
