package com.techindna.eventsync.dto;

public class Meta {
    private int total;
    private int page;
    private int size;

    public Meta() {
    }

    public Meta(int total, int page, int size) {
        this.total = total;
        this.page = page;
        this.size = size;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
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
}
