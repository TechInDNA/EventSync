package com.techindna.eventsync.dto;

import java.util.List;

public class GetQuestionListResponseDto {
  private List<QuestionResponseDto> data;
  private Meta meta;

  public GetQuestionListResponseDto(List<QuestionResponseDto> data, int total, int page, int size) {
    this.data = data;
    this.meta = new Meta(total, page, size);
  }

  public List<QuestionResponseDto> getData() {
    return data;
  }

  public void setData(List<QuestionResponseDto> data) {
    this.data = data;
  }

  public Meta getMeta() {
    return meta;
  }

  public void setMeta(Meta meta) {
    this.meta = meta;
  }
}
