package com.idoceb00.eventory.backend.domain.service;

public class DuplicateReservationException extends RuntimeException {

  public DuplicateReservationException(String message) {
    super(message);
  }
}
