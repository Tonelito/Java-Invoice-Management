package com.is4tech.invoicemanagement.exception;

import com.is4tech.invoicemanagement.annotations.EntityName;
import com.is4tech.invoicemanagement.service.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class AppExceptionHandler {

    @Autowired
    private AuditService auditService;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Object targetObject = ex.getBindingResult().getTarget();
        if (targetObject != null && targetObject.getClass().isAnnotationPresent(EntityName.class)) {
            EntityName entityNameAnnotation = targetObject.getClass().getAnnotation(EntityName.class);
            String entityName = entityNameAnnotation.value();
            StringBuilder errorMessageBuilder = new StringBuilder();

            for (String message : errors.values()) {
                if (!errorMessageBuilder.isEmpty()) {
                    errorMessageBuilder.append(", ");
                }
                errorMessageBuilder.append(message);
            }

            ValidationErrorException validationError = new ValidationErrorException(errorMessageBuilder.toString());
            auditService.logAudit(targetObject, this.getClass().getMethods()[0], validationError, HttpStatus.BAD_REQUEST.value(), entityName, request);
        }

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}