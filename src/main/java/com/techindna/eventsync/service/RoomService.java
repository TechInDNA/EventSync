package com.techindna.eventsync.service;

import com.techindna.eventsync.dto.PaginationRequestDto;
import com.techindna.eventsync.entity.Room;
import com.techindna.eventsync.exception.BadRequestException;
import com.techindna.eventsync.exception.ConflictException;
import com.techindna.eventsync.exception.NotFoundException;
import com.techindna.eventsync.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RoomService {
    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public Room createRoom(String name) {
        Room newRoom = roomRepository.saveRoom(name);

        if (newRoom.getId() == null) {
            throw new ConflictException(String.format("Room '%s' already exists", name));
        }
        return newRoom;
    }

    public List<Room> getAllRooms(PaginationRequestDto pagination) {
        return roomRepository.findAllRooms(pagination.getOffset(), pagination.getLimit());
    }

    public int countRooms() {
        return roomRepository.countRooms();
    }

    public void deleteRoom(UUID id) {
        UUID roomId = roomRepository.deleteRoom(id);

        if (roomId == null){
            throw new NotFoundException(String.format("Room %s not found", id));
        }
    }
}