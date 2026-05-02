package com.techindna.eventsync.dto;

import com.techindna.eventsync.entity.Event;

import java.util.List;

public class GetEventListResponseDto {
    private List<Event> data;
    private Meta meta;

    public GetEventListResponseDto(List<Event> data, int total) {
        this.data = data;
        this.meta = new Meta(total);
    }

    public List<Event> getData() {
        return data;
    }

    public void setData(List<Event> data) {
        this.data = data;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }
}
