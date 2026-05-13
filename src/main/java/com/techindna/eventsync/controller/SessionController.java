package com.techindna.eventsync.controller;

import com.techindna.eventsync.dto.GetSessionListResponseDto;
import com.techindna.eventsync.dto.Meta;
import com.techindna.eventsync.dto.PaginationRequestDto;
import com.techindna.eventsync.entity.Session;
import com.techindna.eventsync.exception.BadRequestException;
import com.techindna.eventsync.exception.InternalServerErrorException;
import com.techindna.eventsync.service.SessionService;
import com.techindna.eventsync.validator.DataValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}
