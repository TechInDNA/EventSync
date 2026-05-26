package com.techindna.eventsync.dto.rooms;

import com.techindna.eventsync.entity.Room;
import java.util.List;

public class GetRoomListResponseDto {
  private List<Room> rooms;
  private int total;
  private int page;
  private int size;

  public GetRoomListResponseDto(List<Room> rooms, int total, int page, int size) {
    this.rooms = rooms;
    this.total = total;
    this.page = page;
    this.size = size;
  }

  public List<Room> getRooms() {
    return rooms;
  }

  public int getTotal() {
    return total;
  }

  public int getPage() {
    return page;
  }

  public int getSize() {
    return size;
  }
}
