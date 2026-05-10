package com.techindna.eventsync.controller;

import com.techindna.eventsync.dto.GetSpeakerListResponseDto;
import com.techindna.eventsync.dto.PaginationRequestDto;
import com.techindna.eventsync.dto.SpeakerRequestDto;
import com.techindna.eventsync.dto.SpeakerResponseDto;
import com.techindna.eventsync.exception.BadRequestException;
import com.techindna.eventsync.exception.ConflictException;
import com.techindna.eventsync.exception.InternalServerErrorException;
import com.techindna.eventsync.exception.NotFoundException;
import com.techindna.eventsync.service.SpeakerService;
import com.techindna.eventsync.validator.DataValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/speakers")
public class SpeakersController {
    private final SpeakerService speakerService;
    private final DataValidator dataValidator;

    public SpeakersController(SpeakerService speakerService, DataValidator dataValidator){
        this.speakerService = speakerService;
        this.dataValidator = dataValidator;
    }
    @GetMapping
    public ResponseEntity<?> getAllSpeakers(
            @RequestParam(required = false, defaultValue = "1") String page,
            @RequestParam(required = false, defaultValue = "5") String size) {
        try {
            dataValidator.validatePageAndSize(page, size);
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

    @PostMapping
    public ResponseEntity<?> createSpeaker(@RequestBody SpeakerRequestDto request) {
        try {
            SpeakerResponseDto speaker = speakerService.createSpeaker(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(speaker);
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred");
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSpeaker(@PathVariable String id, @RequestBody SpeakerRequestDto request) {
        try {
            dataValidator.validateUUID(id);
            SpeakerResponseDto updated = speakerService.updateSpeaker(UUID.fromString(id), request);

            return ResponseEntity.ok().body(updated);

        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (ConflictException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).
                    body(e.getMessage());
        }
        catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred");
        }
    }

    @DeleteMapping(value = {"/{id}", "/"})
    public ResponseEntity<?> deleteSpeaker(@PathVariable(required = false) String id) {
        try {
            dataValidator.validateUUID(id);
            speakerService.deleteSpeaker(UUID.fromString(id));

            return ResponseEntity.status(HttpStatus.OK).body("Speaker " + id + " deleted.");
        }catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred");
        }

    }

}
