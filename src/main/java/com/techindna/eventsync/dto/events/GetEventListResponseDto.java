package com.techindna.eventsync.dto.events;

import com.techindna.eventsync.dto.Meta;
import com.techindna.eventsync.entity.Event;

import java.util.List;

public class GetEventListResponseDto {
    private List<Event> data;
    private Meta meta;

    public GetEventListResponseDto(List<Event> data, int total, int page, int size) {
        this.data = data;
        this.meta = new Meta(total, page, size);
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
