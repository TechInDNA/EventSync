package com.techindna.eventsync.service;

import com.techindna.eventsync.dto.PaginationRequestDto;
import com.techindna.eventsync.entity.Room;
import com.techindna.eventsync.exception.ConflictException;
import com.techindna.eventsync.exception.NotFoundException;
import com.techindna.eventsync.repository.RoomRepository;
import com.techindna.eventsync.validator.DataValidator;
import org.springframework.stereotype.Service;

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

    public Room createRoom(String name) {
        dataValidator.validateRoomData(name);

        return roomRepository.saveRoom(name)
                .orElseThrow(() -> new ConflictException(String.format("Room '%s' already exists.", name)));
    }

    public List<Room> getAllRooms(PaginationRequestDto pagination) {
        return roomRepository.getAllRooms(pagination.getOffset(), pagination.getLimit());
    }

    public int countRooms() {
        return roomRepository.countRooms();
    }

    public Room updateRoom(UUID id, String name) {
        if (roomRepository.findRoomByName(name).isPresent()){
            throw new ConflictException(String.format("Room '%s' already exists.", name));
        }
        return roomRepository.updateRoomById(id, name)
                .orElseThrow(()  -> new NotFoundException(String.format("Room %s not found.", id)));
    }

    public void deleteRoomById(UUID id) {
        UUID roomId = roomRepository.deleteRoomById(id);

        if (roomId == null){
            throw new NotFoundException(String.format("Room %s not found", id));
        }
    }
}