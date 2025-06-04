package com.viettel.spring.cloud.server.exception;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.FORBIDDEN.value());
        response.put("error", "Forbidden");
        response.put("message", "You do not have permission to access this resource.");
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(AuthenticationException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.UNAUTHORIZED.value());
        response.put("error", "Unauthorized");
        response.put("message", "Authentication is required to access this resource.");
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("error", "Not Found");
        response.put("message", "The requested static resource was not found: " + ex.getResourcePath());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Object> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.METHOD_NOT_ALLOWED.value());
        response.put("error", "Method Not Allowed");
        response.put("message", "HTTP method not supported for this endpoint: " + ex.getMethod());

        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.CONFLICT.value());
        response.put("error", "Data Integrity Violation");
        
        String message = ex.getMostSpecificCause().getMessage();
        
        // Gợi ý: nếu bạn muốn lọc hoặc tùy chỉnh thông báo rõ hơn
        if (message != null && message.contains("duplicate key value")) {
            response.put("message", "Duplicated value: a record with the same unique key already exists.");
        } else {
            response.put("message", "Data integrity violation occurred.");
        }

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<Object> handleInvalidDataAccessApiUsage(InvalidDataAccessApiUsageException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Invalid Data Access");
        
        String message = ex.getMessage();
        
        // Check if it's an enum constant error
        if (message != null && message.contains("No enum constant")) {
            // Extract enum class name and invalid value for more details
            String enumClass = "unknown";
            String invalidValue = "unknown";
            
            try {
                // Extract enum class name (e.g., "UserEntity.Role")
                if (message.contains("No enum constant ")) {
                    String enumPart = message.substring(message.indexOf("No enum constant ") + 17);
                    if (enumPart.contains(".")) {
                        String[] parts = enumPart.split("\\.");
                        if (parts.length >= 2) {
                            enumClass = parts[parts.length - 2] + "." + parts[parts.length - 1].split(" ")[0];
                            invalidValue = parts[parts.length - 1];
                        }
                    }
                }
            } catch (Exception e) {
                // Keep default values if parsing fails
            }
            
            response.put("message", "Invalid enum value provided. The value does not exist in the enum definition.");
            response.put("enumClass", enumClass);
            response.put("invalidValue", invalidValue);
            response.put("hint", "Please verify the enum value is spelled correctly and exists in the enum definition.");
        } else if (message != null && message.contains("enum")) {
            response.put("message", "Invalid enum value provided. Please check the allowed values.");
        } else {
            response.put("message", "Invalid data access operation occurred.");
        }
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // logger.warn("Unexpected error occurred", ex);
        Map<String, String> fieldErrors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .collect(Collectors.toMap(
                error -> error.getField(),
                error -> error.getDefaultMessage()
                // (msg1, msg2) -> msg1 
            ));

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Bad Request");
        response.put("message", "Validation failed");
        response.put("errors", fieldErrors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleInvalidJson(HttpMessageNotReadableException ex) {
        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("error", "Bad Request");

        if (ex.getCause() instanceof UnrecognizedPropertyException) {
            UnrecognizedPropertyException unrecognized = (UnrecognizedPropertyException) ex.getCause();
            List<String> unknownFields = new ArrayList<>();
            unknownFields.add(unrecognized.getPropertyName());  

            // Get known properties
            // if (unrecognized.getKnownPropertyIds() != null) {
            //     responseBody.put("knownFields", unrecognized.getKnownPropertyIds());
            // }

            responseBody.put("message", "Unrecognized field(s) in JSON: " + unknownFields);
            responseBody.put("unknownFields", unknownFields);
        } else {
            responseBody.put("message", "Invalid JSON structure");
        }

        responseBody.put("status", 400);
        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Bad Request");
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex) {
        logger.warn("Resource not found: {}", ex.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("error", "Not Found");
        response.put("message", ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralException(Exception ex) {
        logger.error("Unexpected error occurred: ", ex);
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("error", "Internal Server Error");
        response.put("message", "Something went wrong, please try again later.");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
