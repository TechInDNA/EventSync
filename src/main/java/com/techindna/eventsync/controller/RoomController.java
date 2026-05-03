package com.techindna.eventsync.controller;

import com.techindna.eventsync.dto.GetRoomListResponseDto;
import com.techindna.eventsync.dto.PaginationRequestDto;
import com.techindna.eventsync.dto.RoomRequestDto;
import com.techindna.eventsync.entity.Room;
import com.techindna.eventsync.exception.*;
import com.techindna.eventsync.service.RoomService;
import com.techindna.eventsync.validator.PaginationValidator;
import com.techindna.eventsync.validator.StringValidator;
import com.techindna.eventsync.validator.UUIDValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rooms")
public class RoomController {



    private final RoomService roomService;
    private final StringValidator stringValidator;
    private final PaginationValidator paginationValidator;
    private final UUIDValidator uuidValidator;


    public RoomController(RoomService roomService,
                          StringValidator stringValidator,
                          PaginationValidator paginationValidator, UUIDValidator uuidValidator) {
        this.roomService = roomService;
        this.stringValidator = stringValidator;
        this.paginationValidator = paginationValidator;
        this.uuidValidator = uuidValidator;
    }


    @PostMapping
    public ResponseEntity<?> createRoom(@RequestBody RoomRequestDto request) {
        try {
            stringValidator.validateRoomData(
                    request.getName()
            );

            Room newRoom = roomService.createRoom(
                    request.getName()

            );
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

    @GetMapping({"/{id}", ""})
    public ResponseEntity<?> getRooms(
            @PathVariable(required = false) String id,
            @RequestParam(required = false, defaultValue = "1") String page,
            @RequestParam(required = false, defaultValue = "5") String size) {
        try {

            if (id != null) {
                uuidValidator.validateUUID(id);
                Room room = roomService.getRoomById(UUID.fromString(id));
                return ResponseEntity.ok(room);
            }
            paginationValidator.validatePageAndSize(page, size);
            int pageVal = Integer.parseInt(page);
            int sizeVal = Integer.parseInt(size);

            PaginationRequestDto pagination = new PaginationRequestDto(pageVal, sizeVal);
            List<Room> rooms = roomService.getAllRooms(pagination);
            int total = roomService.countRooms();

            GetRoomListResponseDto response = new GetRoomListResponseDto(rooms, total, pageVal, sizeVal);
            return ResponseEntity.ok(response);

        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }




}


