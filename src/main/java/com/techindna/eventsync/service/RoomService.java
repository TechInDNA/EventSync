package com.techindna.eventsync.service;

import com.techindna.eventsync.dto.PaginationRequestDto;
import com.techindna.eventsync.dto.rooms.GetRoomListResponseDto;
import com.techindna.eventsync.dto.rooms.RoomRequestDto;
import com.techindna.eventsync.entity.Room;
import com.techindna.eventsync.exception.ConflictException;
import com.techindna.eventsync.exception.NotFoundException;
import com.techindna.eventsync.repository.RoomRepository;
import com.techindna.eventsync.validator.DataValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class RoomService {
    private final DataValidator dataValidator;
    private final RoomRepository roomRepository;
    private final AuthService authService;

    public RoomService(RoomRepository roomRepository, DataValidator dataValidator, AuthService authService) {
        this.roomRepository = roomRepository;
        this.dataValidator = dataValidator;
        this.authService = authService;
    }

    @Transactional
    public Room createRoom(RoomRequestDto request) {
        dataValidator.validateRoomData(request);

        return roomRepository.saveRoom(request.getName())
                .orElseThrow(() -> new ConflictException(String.format("Room '%s' already exists.", request.getName())));
    }

    @Transactional(readOnly = true)
    public GetRoomListResponseDto getAllRooms(String page, String size, String ipAddress) {
        authService.checkClient(ipAddress);
        dataValidator.validatePageAndSize(page, size);

        int pageVal = Integer.parseInt(page);
        int sizeVal = Integer.parseInt(size);
        PaginationRequestDto pagination = new PaginationRequestDto(pageVal, sizeVal);
        List<Room> rooms = roomRepository.getAllRooms(pagination.getOffset(), pagination.getLimit());
        return new GetRoomListResponseDto(rooms, roomRepository.countRooms(), pageVal, sizeVal);
    }

    public Room updateRoomById(String id, RoomRequestDto request) {
        dataValidator.validateUUID(id);
        dataValidator.validateRoomData(request);

        if (roomRepository.findRoomByName(request.getName()).isPresent()) {
            throw new ConflictException(String.format("Room '%s' already exists.", request.getName()));
        }
        return roomRepository.updateRoomById(UUID.fromString(id), request.getName())
                .orElseThrow(() -> new NotFoundException(String.format("Room %s not found.", id)));
    }

    public void deleteRoomById(String id) {
        dataValidator.validateUUID(id);
        roomRepository.deleteRoomById(UUID.fromString(id))
                .orElseThrow(() -> new NotFoundException(String.format("Room %s not found.", id)));
    }
}