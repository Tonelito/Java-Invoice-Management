package com.is4tech.invoicemanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNorFoundException extends RuntimeException {

    private String resourceName;
    private String fieldName;
    private String fielValue;

    public ResourceNorFoundException(String resourceName, String fieldName, String fielValue) {
        super(String.format("%s was not found with: %s = '%s'", resourceName, fieldName, fielValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fielValue = fielValue;
    }

    public ResourceNorFoundException(String resourceName) {
        super(String.format("There are no %s records in the system", resourceName));
        this.resourceName = resourceName;
    }

}
