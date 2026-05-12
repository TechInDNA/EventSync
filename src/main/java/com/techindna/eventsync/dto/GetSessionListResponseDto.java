package com.techindna.eventsync.dto;

import java.util.List;

public class GetSessionListResponseDto<T> {
    private final List<T> data;
    private final Meta meta;

    public GetSessionListResponseDto(List<T> data, int total, int page, int size) {
        this.data = data;
        this.meta = new Meta(total, page, size);
    }

    public List<T> getData() {
        return data;
    }

    public Meta getMeta() {
        return meta;
    }
}
