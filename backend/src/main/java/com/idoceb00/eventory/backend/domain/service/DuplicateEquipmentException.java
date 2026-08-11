package com.idoceb00.eventory.backend.domain.service;

public class DuplicateEquipmentException extends RuntimeException {

  public DuplicateEquipmentException(String message) {
    super(message);
  }
}
