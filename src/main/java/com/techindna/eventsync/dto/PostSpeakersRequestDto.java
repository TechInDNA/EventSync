package com.techindna.eventsync.dto;

import java.util.List;

public class PostSpeakersRequestDto {
    private SpeakerRequestDto speakerRequestDto;
    private List<ExternalLinkDto> externalLinks;

    public List<ExternalLinkDto> getExternalLinks() {
        return externalLinks;
    }

    public void setExternalLinks(List<ExternalLinkDto> externalLinks) {
        this.externalLinks = externalLinks;
    }

    public SpeakerRequestDto getSpeakerRequestDto() {
        return speakerRequestDto;
    }

    public void setSpeakerRequestDto(SpeakerRequestDto speakerRequestDto) {
        this.speakerRequestDto = speakerRequestDto;
    }
}
