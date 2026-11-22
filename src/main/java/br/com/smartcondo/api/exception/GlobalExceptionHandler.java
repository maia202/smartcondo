package br.com.smartcondo.api.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> tratarValidacao(MethodArgumentNotValidException exception) {
        List<ValidationFieldError> errors = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> new ValidationFieldError(error.getField(), error.getDefaultMessage()))
                .toList();
        return ResponseEntity.badRequest().body(new ValidationErrorResponse(errors));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> tratarNaoEncontrado(NoSuchElementException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", exception.getMessage() == null
                        ? "Recurso não encontrado." : exception.getMessage()));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> tratarStatus(ResponseStatusException exception) {
        return ResponseEntity.status(exception.getStatusCode())
                .body(Map.of("message", exception.getReason() == null ? "Não foi possível processar a solicitação." : exception.getReason()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> tratarIntegridade(DataIntegrityViolationException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message", "Este registro não pode ser removido ou salvo porque há dados relacionados."));
    }

    public record ValidationErrorResponse(List<ValidationFieldError> errors) {
    }

    public record ValidationFieldError(String field, String message) {
    }
}
