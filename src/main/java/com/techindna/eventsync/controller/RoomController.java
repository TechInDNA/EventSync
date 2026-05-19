package com.techindna.eventsync.controller;

import com.techindna.eventsync.dto.rooms.RoomRequestDto;
import com.techindna.eventsync.exception.*;
import com.techindna.eventsync.service.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }


    @PostMapping
    public ResponseEntity<?> createRoom(@RequestBody RoomRequestDto request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(roomService.createRoom(request));
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

    @GetMapping
    public ResponseEntity<?> getAllRooms(
            @RequestParam(required = false, defaultValue = "1") String page,
            @RequestParam(required = false, defaultValue = "10") String size) {
        try {

            return ResponseEntity.status(HttpStatus.OK)
                    .body(roomService.getAllRooms(page, size));

        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (InternalServerErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred, please try again later");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRoom(@PathVariable String id, @RequestBody RoomRequestDto request) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(roomService.updateRoomById(id, request));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        } catch (InternalServerErrorException e) {
            return ResponseEntity.internalServerError()
                    .body("An unexpected error occurred, please try again later");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRoomById(@PathVariable String id) {
        try {

            roomService.deleteRoomById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (InternalServerErrorException e) {
            return ResponseEntity.internalServerError()
                    .body("An unexpected error occurred, please try again later");
        }
    }

}


