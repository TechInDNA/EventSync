package com.techindna.eventsync.dto;

public class PaginationRequestDto {
  private int page;
  private int size;

  public PaginationRequestDto(int page, int size) {
    this.page = page;
    this.size = size;
  }

  public int getPage() {
    return page;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public int getOffset() {
    return (page - 1) * size;
  }

  public int getLimit() {
    return size;
  }
}
