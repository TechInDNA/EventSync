package com.techindna.eventsync.controller;

import com.techindna.eventsync.dto.SessionRequestDto;
import com.techindna.eventsync.entity.EventRef;
import com.techindna.eventsync.entity.Room;
import com.techindna.eventsync.entity.Session;
import com.techindna.eventsync.exception.BadRequestException;
import com.techindna.eventsync.exception.ConflictException;
import com.techindna.eventsync.exception.NotFoundException;
import com.techindna.eventsync.service.QuestionService;
import com.techindna.eventsync.service.SessionService;
import com.techindna.eventsync.validator.DataValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SessionController.class)
class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SessionService sessionService;

    @MockitoBean
    private QuestionService questionService;

    @MockitoBean
    private DataValidator dataValidator;

    private Session sampleSession;
    private final UUID sessionId = UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479");
    private final UUID roomId = UUID.fromString("c1c12204-4427-4add-b755-b681719d1685");
    private final UUID eventId = UUID.fromString("b3b958ac-bdd7-481a-b8f7-636d43794f83");

    @BeforeEach
    void setUp() {
        Room room = new Room();
        room.setId(roomId);
        room.setName("Grand Ballroom");

        EventRef event = new EventRef();
        event.setId(eventId);
        event.setTitle("Tech Conference 2026");

        sampleSession = new Session();
        sampleSession.setId(sessionId);
        sampleSession.setTitle("Keynote: Future of Tech");
        sampleSession.setDescription("Opening keynote on future technology trends");
        sampleSession.setStartDate(Instant.parse("2026-06-15T09:00:00Z"));
        sampleSession.setEndDate(Instant.parse("2026-06-15T10:30:00Z"));
        sampleSession.setRoom(room);
        sampleSession.setCapacity(500);
        sampleSession.setEvent(event);
    }

    @Test
    void getAllSessions_shouldReturnSessionList() throws Exception {
        when(sessionService.getAllSessions(any(), any(), any(), any(), any())).thenReturn(List.of(sampleSession));

        mockMvc.perform(get("/sessions")
                        .param("page", "1")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(sessionId.toString()))
                .andExpect(jsonPath("$[0].title").value("Keynote: Future of Tech"));
    }

    @Test
    void getAllSessions_shouldReturnBadRequest_whenInvalidPage() throws Exception {
        doThrow(new BadRequestException("The page and the size parameter must be a digit greater than 0."))
                .when(dataValidator).validatePageAndSize("0", "20");

        mockMvc.perform(get("/sessions")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getSessionById_shouldReturnSession() throws Exception {
        when(sessionService.getSessionById(sessionId)).thenReturn(sampleSession);

        mockMvc.perform(get("/sessions/{id}", sessionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sessionId.toString()))
                .andExpect(jsonPath("$.title").value("Keynote: Future of Tech"));
    }

    @Test
    void getSessionById_shouldReturnNotFound() throws Exception {
        when(sessionService.getSessionById(sessionId))
                .thenThrow(new NotFoundException("Session " + sessionId + " not found."));

        mockMvc.perform(get("/sessions/{id}", sessionId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getSessionById_shouldReturnBadRequest_whenInvalidUuid() throws Exception {
        doThrow(new BadRequestException("Invalid UUID format."))
                .when(dataValidator).validateUUID("invalid-uuid");

        mockMvc.perform(get("/sessions/invalid-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createSession_shouldReturnCreated() throws Exception {
        SessionRequestDto request = new SessionRequestDto();
        request.setTitle("New Session");
        request.setDescription("A brand new session");
        request.setStartDate("2026-07-01T09:00:00Z");
        request.setEndDate("2026-07-01T11:00:00Z");
        SessionRequestDto.RoomRef roomRef = new SessionRequestDto.RoomRef();
        roomRef.setId(roomId.toString());
        request.setRoom(roomRef);
        request.setCapacity(100);
        SessionRequestDto.EventRef eventRef = new SessionRequestDto.EventRef();
        eventRef.setId(eventId.toString());
        request.setEvent(eventRef);

        when(sessionService.createSession(anyString(), anyString(), any(), any(), any(), anyInt(), any()))
                .thenReturn(sampleSession);

        mockMvc.perform(post("/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(sessionId.toString()));
    }

    @Test
    void createSession_shouldReturnConflict_whenTitleExists() throws Exception {
        SessionRequestDto request = new SessionRequestDto();
        request.setTitle("Existing Session");
        request.setDescription("Duplicate session");
        request.setStartDate("2026-07-01T09:00:00Z");
        request.setEndDate("2026-07-01T11:00:00Z");
        SessionRequestDto.RoomRef roomRef = new SessionRequestDto.RoomRef();
        roomRef.setId(roomId.toString());
        request.setRoom(roomRef);
        request.setCapacity(100);
        SessionRequestDto.EventRef eventRef = new SessionRequestDto.EventRef();
        eventRef.setId(eventId.toString());
        request.setEvent(eventRef);

        when(sessionService.createSession(anyString(), anyString(), any(), any(), any(), anyInt(), any()))
                .thenThrow(new ConflictException("Session with title 'Existing Session' already exists"));

        mockMvc.perform(post("/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void createSession_shouldReturnBadRequest_whenValidationFails() throws Exception {
        SessionRequestDto request = new SessionRequestDto();
        request.setTitle("");
        request.setDescription("Bad session");
        request.setStartDate("2026-07-01T09:00:00Z");
        request.setEndDate("2026-07-01T11:00:00Z");
        SessionRequestDto.RoomRef roomRef = new SessionRequestDto.RoomRef();
        roomRef.setId(roomId.toString());
        request.setRoom(roomRef);
        request.setCapacity(100);
        SessionRequestDto.EventRef eventRef = new SessionRequestDto.EventRef();
        eventRef.setId(eventId.toString());
        request.setEvent(eventRef);

        doThrow(new BadRequestException("The field title is required and cannot be blank."))
                .when(dataValidator).validateSessionData(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyInt());

        mockMvc.perform(post("/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateSession_shouldReturnOk() throws Exception {
        SessionRequestDto request = new SessionRequestDto();
        request.setTitle("Updated Session");
        request.setDescription("Updated description");
        request.setStartDate("2026-08-01T09:00:00Z");
        request.setEndDate("2026-08-01T11:00:00Z");
        SessionRequestDto.RoomRef roomRef = new SessionRequestDto.RoomRef();
        roomRef.setId(roomId.toString());
        request.setRoom(roomRef);
        request.setCapacity(150);
        SessionRequestDto.EventRef eventRef = new SessionRequestDto.EventRef();
        eventRef.setId(eventId.toString());
        request.setEvent(eventRef);

        Session updated = new Session();
        updated.setId(sessionId);
        updated.setTitle("Updated Session");
        updated.setDescription("Updated description");
        updated.setStartDate(Instant.parse("2026-08-01T09:00:00Z"));
        updated.setEndDate(Instant.parse("2026-08-01T11:00:00Z"));
        updated.setCapacity(150);
        Room r = new Room();
        r.setId(roomId);
        updated.setRoom(r);
        EventRef e = new EventRef();
        e.setId(eventId);
        updated.setEvent(e);

        when(sessionService.updateSession(any(), anyString(), anyString(), any(), any(), any(), anyInt(), any()))
                .thenReturn(updated);

        mockMvc.perform(put("/sessions/{id}", sessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sessionId.toString()))
                .andExpect(jsonPath("$.title").value("Updated Session"));
    }

    @Test
    void updateSession_shouldReturnNotFound() throws Exception {
        SessionRequestDto request = new SessionRequestDto();
        request.setTitle("Non Existent");
        request.setDescription("Wont find me");
        request.setStartDate("2026-08-01T09:00:00Z");
        request.setEndDate("2026-08-01T11:00:00Z");
        SessionRequestDto.RoomRef roomRef = new SessionRequestDto.RoomRef();
        roomRef.setId(roomId.toString());
        request.setRoom(roomRef);
        request.setCapacity(50);
        SessionRequestDto.EventRef eventRef = new SessionRequestDto.EventRef();
        eventRef.setId(eventId.toString());
        request.setEvent(eventRef);

        UUID missingId = UUID.randomUUID();
        when(sessionService.updateSession(any(), anyString(), anyString(), any(), any(), any(), anyInt(), any()))
                .thenThrow(new NotFoundException("Session " + missingId + " not found."));

        mockMvc.perform(put("/sessions/{id}", missingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteSession_shouldReturnNoContent() throws Exception {
        when(sessionService.deleteSessionById(sessionId)).thenReturn(sessionId);

        mockMvc.perform(delete("/sessions/{id}", sessionId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteSession_shouldReturnNotFound() throws Exception {
        UUID missingId = UUID.randomUUID();
        when(sessionService.deleteSessionById(missingId))
                .thenThrow(new NotFoundException("Session " + missingId + " not found."));

        mockMvc.perform(delete("/sessions/{id}", missingId))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteSession_shouldReturnBadRequest_whenInvalidUuid() throws Exception {
        doThrow(new BadRequestException("Invalid UUID format."))
                .when(dataValidator).validateUUID("bad-uuid");

        mockMvc.perform(delete("/sessions/bad-uuid"))
                .andExpect(status().isBadRequest());
    }
}
