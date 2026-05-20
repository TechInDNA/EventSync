package com.techindna.eventsync.controller;

import com.techindna.eventsync.dto.speaker.PostSpeakersRequestDto;
import com.techindna.eventsync.dto.speaker.SpeakerRequestDto;
import com.techindna.eventsync.exception.BadRequestException;
import com.techindna.eventsync.exception.ConflictException;
import com.techindna.eventsync.exception.InternalServerErrorException;
import com.techindna.eventsync.exception.NotFoundException;
import com.techindna.eventsync.service.SpeakerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/speakers")
public class SpeakersController {
    private final SpeakerService speakerService;

    public SpeakersController(SpeakerService speakerService){
        this.speakerService = speakerService;
    }

    @GetMapping
    public ResponseEntity<?> getAllSpeakers(
            @RequestParam(required = false, defaultValue = "1") String page,
            @RequestParam(required = false, defaultValue = "10") String size,
            @RequestParam(required = false) String search) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(speakerService.getAllSpeakers(page, size, search));

        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (InternalServerErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred, please try again later");
        }
    }

    @PostMapping
    public ResponseEntity<?> createSpeaker(@RequestBody PostSpeakersRequestDto request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(speakerService.createSpeaker(request));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        } catch (InternalServerErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred, please try again later");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSpeaker(@PathVariable String id, @RequestBody SpeakerRequestDto request) {
        try {

            return ResponseEntity.status(HttpStatus.OK)
                    .body(speakerService.updateSpeakerById(id, request));

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
        } catch (InternalServerErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred, please try again later");
        }
    }

    @DeleteMapping(value = {"/{id}"})
    public ResponseEntity<?> deleteSpeaker(@PathVariable String id) {
        try {
            speakerService.deleteSpeaker(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (InternalServerErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred, please try again later");
        }

    }

}
