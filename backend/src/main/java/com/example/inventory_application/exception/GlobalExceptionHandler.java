package com.example.inventory_application.exception;

import com.example.inventory_application.dto.ErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PartNotFoundException.class)
    public ResponseEntity<ErrorDTO> handlePartNotFound(PartNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ActiveRequestExistsException.class)
    public ResponseEntity<ErrorDTO> handleActiveDispositionExists(ActiveRequestExistsException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorDTO> handleInvalidRequest(InvalidRequestException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    private ResponseEntity<ErrorDTO> buildResponse(HttpStatus status, String message) {
        ErrorDTO body = new ErrorDTO(status.value(), status.getReasonPhrase(), message);
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(IllegalStateTransitionException.class)
    public ResponseEntity<ErrorDTO> handleIllegalStateTransition(IllegalStateTransitionException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }
}
