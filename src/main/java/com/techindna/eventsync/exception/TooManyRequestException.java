package com.techindna.eventsync.exception;

public class TooManyRequestException extends RuntimeException {
  public TooManyRequestException(String message) {
    super(message);
  }
}
