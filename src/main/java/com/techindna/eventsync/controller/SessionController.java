package com.techindna.eventsync.controller;

import com.techindna.eventsync.dto.GetSessionListResponseDto;
import com.techindna.eventsync.dto.PaginationRequestDto;
import com.techindna.eventsync.dto.SessionRequestDto;
import com.techindna.eventsync.entity.Question;
import com.techindna.eventsync.entity.Session;
import com.techindna.eventsync.exception.BadRequestException;
import com.techindna.eventsync.exception.ConflictException;
import com.techindna.eventsync.exception.InternalServerErrorException;
import com.techindna.eventsync.exception.NotFoundException;
import com.techindna.eventsync.exception.UnauthorizedException;
import com.techindna.eventsync.service.QuestionService;
import com.techindna.eventsync.service.SessionService;
import com.techindna.eventsync.validator.DataValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/sessions")
public class SessionController {

    private final SessionService sessionService;
    private final QuestionService questionService;
    private final DataValidator dataValidator;

    public SessionController(SessionService sessionService, QuestionService questionService,
                             DataValidator dataValidator) {
        this.sessionService = sessionService;
        this.questionService = questionService;
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

            return ResponseEntity.status(HttpStatus.OK).body(sessions);

        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (InternalServerErrorException e) {
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

    @PostMapping
    public ResponseEntity<?> createSession(@RequestBody SessionRequestDto request) {
        try {
            dataValidator.validateSessionData(
                    request.getTitle(),
                    request.getDescription(),
                    request.getStartDate(),
                    request.getEndDate(),
                    request.getRoomId(),
                    request.getEventId(),
                    request.getCapacity()
            );

            Session newSession = sessionService.createSession(
                    request.getTitle(),
                    request.getDescription(),
                    Instant.parse(request.getStartDate()),
                    Instant.parse(request.getEndDate()),
                    UUID.fromString(request.getRoomId()),
                    request.getCapacity(),
                    UUID.fromString(request.getEventId())
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(newSession);
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (InternalServerErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSession(
            @PathVariable String id,
            @RequestBody SessionRequestDto request) {
        try {
            dataValidator.validateUUID(id);
            dataValidator.validateSessionData(
                    request.getTitle(),
                    request.getDescription(),
                    request.getStartDate(),
                    request.getEndDate(),
                    request.getRoomId(),
                    request.getEventId(),
                    request.getCapacity()
            );

            Session updatedSession = sessionService.updateSession(
                    UUID.fromString(id),
                    request.getTitle(),
                    request.getDescription(),
                    Instant.parse(request.getStartDate()),
                    Instant.parse(request.getEndDate()),
                    UUID.fromString(request.getRoomId()),
                    request.getCapacity(),
                    UUID.fromString(request.getEventId())
            );

            return ResponseEntity.status(HttpStatus.OK).body(updatedSession);
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (InternalServerErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSession(@PathVariable String id) {
        try {
            dataValidator.validateUUID(id);
            sessionService.deleteSessionById(UUID.fromString(id));
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InternalServerErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/{sessionId}/speaker/{speakerId}")
    public ResponseEntity<?> addSpeakerToSession(
            @PathVariable String sessionId,
            @PathVariable String speakerId,
            @RequestBody Map<String, String> body) {
        try {
            dataValidator.validateUUID(sessionId);
            dataValidator.validateUUID(speakerId);

            String startTime = body.get("startTime");
            String endTime = body.get("endTime");
            dataValidator.validateSpeakerTimes(startTime, endTime);

            UUID sId = UUID.fromString(sessionId);
            UUID spId = UUID.fromString(speakerId);

            sessionService.getSessionById(sId);

            sessionService.addSpeakerToSession(sId, spId,
                    LocalTime.parse(startTime), LocalTime.parse(endTime));

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "sessionId", sId, "speakerId", spId,
                    "startTime", startTime, "endTime", endTime
            ));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InternalServerErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/{sessionId}/speaker/{speakerId}")
    public ResponseEntity<?> updateSpeakerInSession(
            @PathVariable String sessionId,
            @PathVariable String speakerId,
            @RequestBody Map<String, String> body) {
        try {
            dataValidator.validateUUID(sessionId);
            dataValidator.validateUUID(speakerId);

            String startTime = body.get("startTime");
            String endTime = body.get("endTime");
            dataValidator.validateSpeakerTimes(startTime, endTime);

            UUID sId = UUID.fromString(sessionId);
            UUID spId = UUID.fromString(speakerId);

            sessionService.updateSpeakerInSession(sId, spId,
                    LocalTime.parse(startTime), LocalTime.parse(endTime));

            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                    "sessionId", sId, "speakerId", spId,
                    "startTime", startTime, "endTime", endTime
            ));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InternalServerErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/{sessionId}/speaker/{speakerId}")
    public ResponseEntity<?> removeSpeakerFromSession(
            @PathVariable String sessionId,
            @PathVariable String speakerId) {
        try {
            dataValidator.validateUUID(sessionId);
            dataValidator.validateUUID(speakerId);

            sessionService.removeSpeakerFromSession(
                    UUID.fromString(sessionId), UUID.fromString(speakerId));

            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InternalServerErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{id}/questions")
    public ResponseEntity<?> getQuestions(
            @PathVariable String id,
            @RequestParam(required = false, defaultValue = "upvotes") String sort,
            @RequestParam(required = false, defaultValue = "1") String page,
            @RequestParam(required = false, defaultValue = "20") String size) {
        try {
            dataValidator.validateUUID(id);
            dataValidator.validatePageAndSize(page, size);
            if (!"upvotes".equals(sort) && !"createdAt".equals(sort)) {
                throw new BadRequestException("Sort must be 'upvotes' or 'createdAt'.");
            }

            int pageVal = Integer.parseInt(page);
            int sizeVal = Integer.parseInt(size);
            UUID sessionId = UUID.fromString(id);

            PaginationRequestDto pagination = new PaginationRequestDto(pageVal, sizeVal);
            List<Question> questions = questionService.getQuestionsBySessionId(sessionId, sort, pagination);
            int total = questionService.countQuestionsBySessionId(sessionId);

            GetSessionListResponseDto response = new GetSessionListResponseDto(
                    questions, total, pageVal, sizeVal);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (InternalServerErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/questions")
    public ResponseEntity<?> createQuestion(
            @PathVariable String id,
            @RequestBody Map<String, Object> body) {
        try {
            dataValidator.validateUUID(id);
            UUID sessionId = UUID.fromString(id);

            String title = (String) body.get("title");
            String content = (String) body.get("content");
            Boolean isAnonymous = (Boolean) body.getOrDefault("isAnonymous", false);

            dataValidator.checkNullData("content", content);

            Map<String, Object> participantMap = (Map<String, Object>) body.get("participant");
            UUID userId = null;
            if (participantMap != null) {
                String userIdStr = (String) participantMap.get("id");
                if (userIdStr != null) {
                    dataValidator.validateUUID(userIdStr);
                    userId = UUID.fromString(userIdStr);
                }
            }

            Question question = questionService.createQuestion(title, content, sessionId, userId,
                    isAnonymous != null && isAnonymous);

            return ResponseEntity.status(HttpStatus.CREATED).body(question);
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (InternalServerErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/questions/{qid}/upvote")
    public ResponseEntity<?> upvoteQuestion(
            @PathVariable String id,
            @PathVariable String qid) {
        try {
            dataValidator.validateUUID(id);
            dataValidator.validateUUID(qid);

            int upvotes = questionService.upvoteQuestion(UUID.fromString(qid));
            return ResponseEntity.status(HttpStatus.OK).body(upvotes);
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InternalServerErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
