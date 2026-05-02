package com.techindna.eventsync.dto;

public class Meta {
    private int total;
    private int page;

    public Meta() {
    }

    public Meta(int total) {
        this.total = total;
        this.page = 1;
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
}
