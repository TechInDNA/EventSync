package com.techindna.eventsync.controller;

import com.techindna.eventsync.dto.GetSessionListResponseDto;
import com.techindna.eventsync.dto.PaginationRequestDto;
import com.techindna.eventsync.dto.SessionRequestDto;
import com.techindna.eventsync.entity.Session;
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

import java.time.Instant;
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
