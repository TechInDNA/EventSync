package com.techindna.eventsync.dto.sessions;

import com.techindna.eventsync.dto.Meta;
import java.util.List;

public class GetSessionListResponseDto {
  private final List<SessionResponseDto> data;
  private final Meta meta;

  public GetSessionListResponseDto(List<SessionResponseDto> data, int total, int page, int size) {
    this.data = data;
    this.meta = new Meta(total, page, size);
  }

  public List<SessionResponseDto> getData() {
    return data;
  }

  public Meta getMeta() {
    return meta;
  }
}
