package com.minhhn.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class ValidationErrorResponse extends ErrorResponse {
    private Map<String, String> errors = new HashMap<>();
    
    public ValidationErrorResponse(int status, String error, String message, String path) {
        super(status, error, message, path);
    }
    
    public void addValidationError(String field, String message) {
        errors.put(field, message);
    }
}
