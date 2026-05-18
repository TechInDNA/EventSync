package com.techindna.eventsync.service;

import com.techindna.eventsync.dto.PaginationRequestDto;
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

    public RoomService(RoomRepository roomRepository, DataValidator dataValidator) {
        this.roomRepository = roomRepository;
        this.dataValidator = dataValidator;
    }

    public Room createRoom(RoomRequestDto request) {
        dataValidator.validateRoomData(request);

        return roomRepository.saveRoom(request.getName())
                .orElseThrow(() -> new ConflictException(String.format("Room '%s' already exists.", request.getName())));
    }

    @Transactional(readOnly = true)
    public List<Room> getAllRooms(PaginationRequestDto pagination) {
        return roomRepository.getAllRooms(pagination.getOffset(), pagination.getLimit());
    }

    @Transactional(readOnly = true)
    public int countRooms() {
        return roomRepository.countRooms();
    }

    @Transactional
    public Room updateRoomById(String id, RoomRequestDto request) {
        dataValidator.validateUUID(id);
        dataValidator.validateRoomData(request);

        if (roomRepository.findRoomByName(request.getName()).isPresent()) {
            throw new ConflictException(String.format("Room '%s' already exists.", request.getName()));
        }
        return roomRepository.updateRoomById(UUID.fromString(id), request.getName())
                .orElseThrow(() -> new NotFoundException(String.format("Room %s not found.", id)));
    }

    @Transactional
    public void deleteRoomById(String id) {
        dataValidator.validateUUID(id);
        roomRepository.deleteRoomById(UUID.fromString(id))
                .orElseThrow(() -> new NotFoundException(String.format("Room %s not found.", id)));
    }
}