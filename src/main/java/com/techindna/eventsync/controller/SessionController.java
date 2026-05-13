package com.techindna.eventsync.controller;

import com.techindna.eventsync.dto.GetSessionListResponseDto;

import com.techindna.eventsync.dto.PaginationRequestDto;

import com.techindna.eventsync.entity.Session;
import com.techindna.eventsync.exception.*;
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
            @RequestParam(required = false) String room,
            @RequestParam(required = false) String speaker,
            @RequestParam(required = false) Boolean live,
            @RequestParam(required = false) String event,
            @RequestParam(required = false, defaultValue = "1") String page,
            @RequestParam(required = false, defaultValue = "20") String size) {
        try {
            dataValidator.validatePageAndSize(page, size);
            int pageVal = Integer.parseInt(page);
            int sizeVal = Integer.parseInt(size);

            PaginationRequestDto pagination = new PaginationRequestDto(pageVal, sizeVal);
            List<Session> sessions = sessionService.getAllSessions(pagination, room, speaker, live, event);
            int total = sessionService.countSessions(room, speaker, live, event);
            GetSessionListResponseDto<Session> response = new GetSessionListResponseDto<>(
                    sessions,
                    total,
                    pageVal,
                    sizeVal
            );

            return ResponseEntity.ok(response);

        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSessionById(@PathVariable String id) {
        try {
            dataValidator.validateUUID(id);
            Session session = sessionService.getSessionById(UUID.fromString(id));
            return ResponseEntity.status(HttpStatus.OK).body(session);
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InternalServerErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


}
