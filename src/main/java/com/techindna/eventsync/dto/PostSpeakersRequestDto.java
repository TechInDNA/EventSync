package com.techindna.eventsync.dto;

import com.techindna.eventsync.dto.speaker.SpeakerRequestDto;

import java.util.List;

public class PostSpeakersRequestDto extends SpeakerRequestDto {
    private List<ExternalLinkDto> externalLinks;

    public List<ExternalLinkDto> getExternalLinks() {
        return externalLinks;
    }

    public void setExternalLinks(List<ExternalLinkDto> externalLinks) {
        this.externalLinks = externalLinks;
    }
}
