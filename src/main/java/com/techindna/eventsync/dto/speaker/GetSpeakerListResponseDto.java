package com.techindna.eventsync.dto.speaker;


import com.techindna.eventsync.dto.Meta;

import java.util.List;

public class GetSpeakerListResponseDto {
    private final List<SpeakerResponseDto> data;
    private final Meta meta;

    public GetSpeakerListResponseDto(List<SpeakerResponseDto> data, int total, int page, int size) {
        this.data = data;
        this.meta = new Meta(total, page, size);
    }

    public List<SpeakerResponseDto> getData() {
        return data;
    }

    public Meta getMeta() {
        return meta;
    }
}
