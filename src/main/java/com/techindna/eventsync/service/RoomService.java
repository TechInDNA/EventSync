package com.techindna.eventsync.service;

import com.techindna.eventsync.dto.PaginationRequestDto;
import com.techindna.eventsync.entity.Room;
import com.techindna.eventsync.exception.ConflictException;
import com.techindna.eventsync.exception.NotFoundException;
import com.techindna.eventsync.repository.RoomRepository;
import com.techindna.eventsync.validator.DataValidator;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Service
public class RoomService {
    private final DataValidator dataValidator;
    private final RoomRepository roomRepository;
    private final DataSource dataSource;

    public RoomService(RoomRepository roomRepository, DataValidator dataValidator, DataSource dataSource) {
        this.roomRepository = roomRepository;
        this.dataValidator = dataValidator;
        this.dataSource = dataSource;
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

    public Room updateRoomById(UUID id, String name) {
        dataValidator.validateUUID(String.valueOf(id));
        dataValidator.validateRoomData(name);

        try(Connection connection = dataSource.getConnection()){
            if (roomRepository.findRoomByName(name, connection).isPresent()){
                throw new ConflictException(String.format("Room '%s' already exists.", name));
            }
            return roomRepository.updateRoomById(id, name, connection)
                    .orElseThrow(()  -> new NotFoundException(String.format("Room %s not found.", id)));
        } catch (SQLException e){
            throw new InternalAuthenticationServiceException(e.getMessage());
        }
    }

    public void deleteRoomById(UUID id) {
        UUID roomId = roomRepository.deleteRoomById(id);

        if (roomId == null){
            throw new NotFoundException(String.format("Room %s not found", id));
        }
    }
}