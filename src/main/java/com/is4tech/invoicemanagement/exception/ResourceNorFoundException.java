package com.is4tech.invoicemanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import lombok.Getter;
import java.time.LocalDate;

@Getter
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNorFoundException extends RuntimeException {

    private final String resourceName;
    private final String fieldName;
    private final String fielValue;

    public ResourceNorFoundException(String resourceName, String fieldName, String fielValue) {
        super(String.format("%s was not found with: %s = '%s'", resourceName, fieldName, fielValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fielValue = fielValue;
    }

    public ResourceNorFoundException(String resourceName) {
        super(String.format("No se encontraron registros de %s en el sistema", resourceName));
        this.resourceName = resourceName;
        this.fieldName = null;
        this.fielValue = null;
    }

    public static ResourceNorFoundException auditNotFoundWithName(String entity, LocalDate startDate, LocalDate endDate, String fullName) {
        return new ResourceNorFoundException(
                String.format("auditorías para la entidad '%s' en el rango de fechas entre %s y %s para el nombre: %s",
                        entity, startDate, endDate, fullName)
        );
    }

    public static ResourceNorFoundException auditNotFoundWithoutName(String entity, LocalDate startDate, LocalDate endDate) {
        return new ResourceNorFoundException(
                String.format("auditorías para la entidad '%s' en el rango de fechas entre %s y %s",
                        entity, startDate, endDate)
        );
    }
}
