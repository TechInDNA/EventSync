package com.techindna.eventsync.controller;

import com.techindna.eventsync.dto.sessions.GetSessionRequestDto;
import com.techindna.eventsync.dto.speaker.SessionRequestDto;
import com.techindna.eventsync.exception.*;
import com.techindna.eventsync.mapper.SessionMapper;
import com.techindna.eventsync.service.SessionService;
import com.techindna.eventsync.validator.DataValidator;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/sessions")
public class SessionController {
    private final SessionService sessionService;
    private final DataValidator dataValidator;

    public SessionController(SessionService sessionService, DataValidator dataValidator) {
        this.sessionService = sessionService;
        this.dataValidator = dataValidator;
    }

    @GetMapping
    public ResponseEntity<?> getSessions(
            @RequestParam(required = false, defaultValue = "1") String page,
            @RequestParam(required = false, defaultValue = "5") String size,
            @RequestParam(required = false) String speakerName,
            @RequestParam(required = false, defaultValue = "false") boolean isLive,
            @RequestParam(required = false) String eventTitle,
            @RequestParam(required = false) String roomName,
            HttpServletRequest servletRequest
    ) {
        try{
            GetSessionRequestDto request = SessionMapper
                    .mapToGetSessionRequestDto(roomName, speakerName, eventTitle, isLive);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(sessionService.getAllSessions(request, page, size, servletRequest.getRemoteAddr()));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(e.getMessage());
        } catch (InternalServerErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred, please try again later");
        }
    }


    @PostMapping
    public ResponseEntity<?> createSession(@RequestBody SessionRequestDto request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(sessionService.createSession(request));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (InternalServerErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred, please try again later");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSession(@PathVariable String id, @RequestBody SessionRequestDto request) {
        try {
            dataValidator.validateUUID(id);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(sessionService.updateSession(UUID.fromString(id), request));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (InternalServerErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred, please try again later");
        }
    }

    @DeleteMapping({"/{id}"})
    public ResponseEntity<?> deleteSession(@PathVariable String id) {
        try {
            sessionService.deleteSessionById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (InternalServerErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred, please try again later");
        }
    }
}
