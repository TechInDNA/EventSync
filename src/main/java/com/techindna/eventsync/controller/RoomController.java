package com.techindna.eventsync.controller;

import com.techindna.eventsync.dto.GetRoomListResponseDto;
import com.techindna.eventsync.dto.PaginationRequestDto;
import com.techindna.eventsync.dto.RoomRequestDto;
import com.techindna.eventsync.entity.Room;
import com.techindna.eventsync.exception.*;
import com.techindna.eventsync.service.RoomService;
import com.techindna.eventsync.validator.DataValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
            dataValidator.validateRoomData(request.getName());

            Room newRoom = roomService.createRoom(request.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(newRoom);

        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        } catch (InternalServerErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
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
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRoom(@PathVariable String id, @RequestBody RoomRequestDto request) {
        try {
            dataValidator.validateUUID(id);
            dataValidator.validateRoomData(request.getName());

            Room updatedRoom = roomService.updateRoom(UUID.fromString(id), request.getName());
            return ResponseEntity.status(HttpStatus.OK).body(updatedRoom);

        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRoomById(@PathVariable String id) {
        try {
            dataValidator.validateUUID(id);
            roomService.deleteRoomById(UUID.fromString(id));

            return ResponseEntity.status(HttpStatus.OK)
                    .body(String.format("Room %s deleted", id));

        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(e.getMessage());
        }
    }

}


