package com.idoceb00.eventory.backend.domain.service;

public class InvalidStockAdjustmentException extends RuntimeException {

  public InvalidStockAdjustmentException(String message) {
    super(message);
  }
}
