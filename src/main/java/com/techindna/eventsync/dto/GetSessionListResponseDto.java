package com.techindna.eventsync.dto;

import com.techindna.eventsync.entity.Session;

import java.util.List;

public class GetSessionListResponseDto {
    private final List<Session> data;
    private final Meta meta;

    public GetSessionListResponseDto(List<Session> data, int total, int page, int size) {
        this.data = data;
        this.meta = new Meta(total, page, size);
    }

    public List<Session> getData() {
        return data;
    }

    public Meta getMeta() {
        return meta;
    }
}
