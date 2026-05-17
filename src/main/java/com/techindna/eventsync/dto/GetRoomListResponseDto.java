package com.techindna.eventsync.dto;

import com.techindna.eventsync.entity.Room;

import java.util.List;

public class GetRoomListResponseDto {
    private List<Room> data;
    private Meta meta;

    public GetRoomListResponseDto(List<Room> data, int total, int page, int size) {
        this.data = data;
        this.meta = new Meta(total, page, size);
    }

    public List<Room> getData() {
        return data;
    }

    public void setData(List<Room> data) {
        this.data = data;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }
}