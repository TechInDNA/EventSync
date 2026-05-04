package com.techindna.eventsync.service;

import com.techindna.eventsync.entity.Room;
import com.techindna.eventsync.exception.ConflictException;
import com.techindna.eventsync.repository.RoomRepository;
import org.springframework.stereotype.Service;

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
}