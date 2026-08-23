package com.idoceb00.eventory.backend.infrastructure.web.exception;

import com.idoceb00.eventory.backend.domain.service.DuplicateEquipmentException;
import com.idoceb00.eventory.backend.domain.service.DuplicatePerformanceException;
import com.idoceb00.eventory.backend.domain.service.EntityNotFoundException;
import com.idoceb00.eventory.backend.domain.service.EquipmentStateException;
import com.idoceb00.eventory.backend.domain.service.InsufficientStockException;
import com.idoceb00.eventory.backend.domain.service.InvalidStockAdjustmentException;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(EntityNotFoundException.class)
  public ProblemDetail handleNotFound(EntityNotFoundException ex) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    problem.setTitle("Not Found");
    problem.setType(URI.create("about:blank"));
    return problem;
  }

  @ExceptionHandler(InsufficientStockException.class)
  public ProblemDetail handleConflict(InsufficientStockException ex) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    problem.setTitle("Conflict");
    problem.setType(URI.create("about:blank"));
    return problem;
  }

  @ExceptionHandler(InvalidStockAdjustmentException.class)
  public ProblemDetail handleBadStockAdjustment(InvalidStockAdjustmentException ex) {
    ProblemDetail problem =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    problem.setTitle("Bad Request");
    problem.setType(URI.create("about:blank"));
    return problem;
  }

  @ExceptionHandler(EquipmentStateException.class)
  public ProblemDetail handleEquipmentState(EquipmentStateException ex) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    problem.setTitle("Conflict");
    problem.setType(URI.create("about:blank"));
    return problem;
  }

  @ExceptionHandler(DuplicateEquipmentException.class)
  public ProblemDetail handleDuplicateEquipment(DuplicateEquipmentException ex) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    problem.setTitle("Conflict");
    problem.setType(URI.create("about:blank"));
    return problem;
  }

  @ExceptionHandler(DuplicatePerformanceException.class)
  public ProblemDetail handleDuplicatePerformance(DuplicatePerformanceException ex) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    problem.setTitle("Conflict");
    problem.setType(URI.create("about:blank"));
    return problem;
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
    ProblemDetail problem =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    problem.setTitle("Bad Request");
    problem.setType(URI.create("about:blank"));
    return problem;
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
    ProblemDetail problem =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
    problem.setTitle("Bad Request");
    problem.setType(URI.create("about:blank"));

    List<String> errors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(GlobalExceptionHandler::formatFieldError)
            .toList();
    problem.setProperty("errors", errors);
    return problem;
  }

  @ExceptionHandler(AuthenticationException.class)
  public ProblemDetail handleAuthentication(AuthenticationException ex) {
    ProblemDetail problem =
        ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Authentication failed");
    problem.setTitle("Unauthorized");
    problem.setType(URI.create("about:blank"));
    return problem;
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ProblemDetail handleAccessDenied(AccessDeniedException ex) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Access denied");
    problem.setTitle("Forbidden");
    problem.setType(URI.create("about:blank"));
    return problem;
  }

  private static String formatFieldError(FieldError fieldError) {
    return fieldError.getField() + ": " + fieldError.getDefaultMessage();
  }
}
