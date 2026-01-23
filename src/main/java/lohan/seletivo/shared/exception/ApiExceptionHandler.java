package lohan.seletivo.shared.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.List;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiError.of(404, "Not Found", ex.getMessage(), req.getRequestURI())
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiValidationError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<ApiValidationError.FieldErrorItem> fields = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> new ApiValidationError.FieldErrorItem(fe.getField(), fe.getDefaultMessage()))
                .toList();

        return ResponseEntity.badRequest().body(
                ApiValidationError.of(400, "Validation Error", "Invalid request body", req.getRequestURI(), fields)
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
        return ResponseEntity.badRequest().body(
                ApiError.of(400, "Bad Request", ex.getMessage(), req.getRequestURI())
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
        String message = "Corpo da requisicao invalido";
        Throwable cause = ex.getCause();
        if (cause instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException ife) {
            Class<?> targetType = ife.getTargetType();
            if (targetType != null && targetType.isEnum()) {
                Object[] allowed = targetType.getEnumConstants();
                message = "Valor invalido para o campo. Use um dos valores: " + Arrays.toString(allowed);
            }
        }
        return ResponseEntity.badRequest().body(
                ApiError.of(400, "Bad Request", message, req.getRequestURI())
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
        String message = "Violacao de integridade dos dados";
        String detail = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : "";
        if (detail != null && detail.toLowerCase().contains("uk_artists_name")) {
            message = "Artista ja cadastrado com esse nome";
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ApiError.of(409, "Conflict", message, req.getRequestURI())
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest req) {
        // em prod: loga o ex; aqui devolve genérico
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiError.of(500, "Internal Server Error", "Unexpected error", req.getRequestURI())
        );
    }

    public record ApiError(
            OffsetDateTime timestamp,
            int status,
            String error,
            String message,
            String path
    ) {
        public static ApiError of(int status, String error, String message, String path) {
            return new ApiError(OffsetDateTime.now(), status, error, message, path);
        }
    }

    public record ApiValidationError(
            OffsetDateTime timestamp,
            int status,
            String error,
            String message,
            String path,
            List<FieldErrorItem> fields
    ) {
        public static ApiValidationError of(int status, String error, String message, String path, List<FieldErrorItem> fields) {
            return new ApiValidationError(OffsetDateTime.now(), status, error, message, path, fields);
        }

        public record FieldErrorItem(String field, String message) {}
    }
}
