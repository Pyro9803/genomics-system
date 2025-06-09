package com.minhhn.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Error response for validation errors.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ValidationErrorResponse extends ErrorResponse {
    private Map<String, String> validationErrors = new HashMap<>();
    
    /**
     * Creates a new ValidationErrorResponse with the current timestamp.
     */
    public static ValidationErrorResponse of(int status, String error, String message, String path) {
        ValidationErrorResponse response = new ValidationErrorResponse();
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(status);
        response.setError(error);
        response.setMessage(message);
        response.setPath(path);
        return response;
    }
    
    /**
     * Adds a validation error.
     * 
     * @param field The field with the validation error
     * @param message The validation error message
     */
    public void addValidationError(String field, String message) {
        validationErrors.put(field, message);
    }
}
