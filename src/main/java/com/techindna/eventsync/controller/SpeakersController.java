package com.techindna.eventsync.controller;

import com.techindna.eventsync.dto.GetSpeakerListResponseDto;
import com.techindna.eventsync.dto.PaginationRequestDto;
import com.techindna.eventsync.dto.SpeakerResponseDto;
import com.techindna.eventsync.exception.BadRequestException;
import com.techindna.eventsync.exception.InternalServerErrorException;
import com.techindna.eventsync.service.SpeakerService;
import com.techindna.eventsync.validator.PaginationValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/speakers")
public class SpeakersController {
    private final PaginationValidator paginationValidator;
    private final SpeakerService speakerService;

    public SpeakersController(PaginationValidator paginationValidator, SpeakerService speakerService){
        this.paginationValidator = paginationValidator;
        this.speakerService = speakerService;
    }
    @GetMapping
    public ResponseEntity<?> getAllSpeakers(
            @RequestParam(required = false, defaultValue = "1") String page,
            @RequestParam(required = false, defaultValue = "5") String size) {
        try {
            paginationValidator.validatePageAndSize(page, size);
            int pageVal = Integer.parseInt(page);
            int sizeVal = Integer.parseInt(size);

            PaginationRequestDto pagination = new PaginationRequestDto(pageVal, sizeVal);
            List<SpeakerResponseDto> speakers = speakerService.getAllSpeakers(pagination);
            int total = speakerService.countSpeaker();
            GetSpeakerListResponseDto response = new GetSpeakerListResponseDto(speakers, total, pageVal, sizeVal);

            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (InternalServerErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }
}
