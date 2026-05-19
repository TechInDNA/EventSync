package com.techindna.eventsync.controller;

import com.techindna.eventsync.dto.GetRoomListResponseDto;
import com.techindna.eventsync.dto.PaginationRequestDto;
import com.techindna.eventsync.dto.rooms.RoomRequestDto;
import com.techindna.eventsync.entity.Room;
import com.techindna.eventsync.exception.*;
import com.techindna.eventsync.service.RoomService;
import com.techindna.eventsync.validator.DataValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;
    private final DataValidator dataValidator;

    public RoomController(RoomService roomService, DataValidator dataValidator) {

        this.roomService = roomService;
        this.dataValidator = dataValidator;
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
            @RequestParam(required = false, defaultValue = "5") String size) {
        try {

            dataValidator.validatePageAndSize(page, size);
            int pageVal = Integer.parseInt(page);
            int sizeVal = Integer.parseInt(size);

            PaginationRequestDto pagination = new PaginationRequestDto(pageVal, sizeVal);
            List<Room> rooms = roomService.getAllRooms(pagination);
            int total = roomService.countRooms();

            GetRoomListResponseDto response = new GetRoomListResponseDto(rooms, total, pageVal, sizeVal);
            return ResponseEntity.status(HttpStatus.OK).body(response);

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


