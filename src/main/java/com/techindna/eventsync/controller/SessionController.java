package com.techindna.eventsync.controller;

import com.techindna.eventsync.dto.GetSessionListResponseDto;
import com.techindna.eventsync.dto.PaginationRequestDto;
import com.techindna.eventsync.dto.SessionRequestDto;
import com.techindna.eventsync.dto.SessionResponseDto;
import com.techindna.eventsync.exception.BadRequestException;
import com.techindna.eventsync.exception.ConflictException;
import com.techindna.eventsync.exception.InternalServerErrorException;
import com.techindna.eventsync.exception.NotFoundException;
import com.techindna.eventsync.exception.UnauthorizedException;
import com.techindna.eventsync.service.SessionService;
import com.techindna.eventsync.validator.DataValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public ResponseEntity<?> getAllSessions(
            @RequestParam(required = false, defaultValue = "1") String page,
            @RequestParam(required = false, defaultValue = "5") String size) {
        try {
            dataValidator.validatePageAndSize(page, size);
            int pageVal = Integer.parseInt(page);
            int sizeVal = Integer.parseInt(size);

            PaginationRequestDto pagination = new PaginationRequestDto(pageVal, sizeVal);
            List<SessionResponseDto> sessions = sessionService.getAllSessions(pagination);
            int total = sessionService.countSessions();

            GetSessionListResponseDto response = new GetSessionListResponseDto(sessions, total, pageVal, sizeVal);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
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

    @DeleteMapping({"/{id}", "/"})
    public ResponseEntity<?> deleteSession(@PathVariable String id) {
        try {
            dataValidator.validateUUID(id);
            sessionService.deleteSessionById(UUID.fromString(id));
            return ResponseEntity.status(HttpStatus.OK)
                    .body(String.format("Session %s deleted.", id));
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
