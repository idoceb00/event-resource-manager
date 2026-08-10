package com.idoceb00.eventory.backend.domain.service;

public class InsufficientStockException extends RuntimeException {

  public InsufficientStockException(String message) {
    super(message);
  }
}
