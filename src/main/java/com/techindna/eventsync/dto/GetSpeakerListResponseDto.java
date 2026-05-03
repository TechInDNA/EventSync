package com.techindna.eventsync.dto;

import com.techindna.eventsync.entity.Speaker;

import java.util.List;

public class GetSpeakerListResponseDto {
    private final List<Speaker> data;
    private final  Meta meta;

    public GetSpeakerListResponseDto(List<Speaker> data, int total, int page, int size) {
        this.data = data;
        this.meta = new Meta(total, page, size);
    }
}
