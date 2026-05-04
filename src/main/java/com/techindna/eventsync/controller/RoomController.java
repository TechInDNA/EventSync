package com.techindna.eventsync.controller;

import com.techindna.eventsync.dto.RoomRequestDto;
import com.techindna.eventsync.entity.Room;
import com.techindna.eventsync.exception.BadRequestException;
import com.techindna.eventsync.exception.ConflictException;
import com.techindna.eventsync.exception.InternalServerErrorException;
import com.techindna.eventsync.exception.UnauthorizedException;
import com.techindna.eventsync.service.RoomService;
import com.techindna.eventsync.validator.StringValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rooms")
public class RoomController {
    private final RoomService roomService;
    private final StringValidator stringValidator;

    public RoomController(RoomService roomService, StringValidator stringValidator) {
        this.roomService = roomService;
        this.stringValidator = stringValidator;
    }

    @PostMapping
    public ResponseEntity<?> createRoom(@RequestBody RoomRequestDto request) {
        try {
            stringValidator.validateRoomData(request.getName());

            Room newRoom = roomService.createRoom(request.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(newRoom);

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
}