package com.is4tech.invoicemanagement.controller;

import com.is4tech.invoicemanagement.service.AuditService;
import com.is4tech.invoicemanagement.utils.MessagePage;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.is4tech.invoicemanagement.dto.NameSearchDto;
import com.is4tech.invoicemanagement.dto.RolDto;
import com.is4tech.invoicemanagement.exception.ResourceNorFoundException;
import com.is4tech.invoicemanagement.service.RolService;
import com.is4tech.invoicemanagement.utils.Message;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/invoice-management/v0.1/role")
public class RolController {

    @Autowired
    private RolService rolService;

    private static final String NAME_ENTITY = "Role";
    private static final String ID_ENTITY = "Id";
    int statusCode;
    @Autowired
    private AuditService auditService;

    @PostMapping("/create")
    public ResponseEntity<Message> saveRol(@RequestBody @Valid RolDto rolDto, HttpServletRequest request) throws BadRequestException {
        try {
            RolDto rolSave = rolService.saveRol(rolDto, request);

            statusCode = HttpStatus.CREATED.value();
            return new ResponseEntity<>(Message.builder()
                    .note("Saved successfully")
                    .object(rolSave)
                    .build(),
                    HttpStatus.CREATED);

        } catch (DataAccessException e) {
            statusCode = HttpStatus.BAD_REQUEST.value();
            throw new BadRequestException("Error saving record: " + e.getMessage());
        } catch (ResourceNorFoundException e) {
            statusCode = HttpStatus.NOT_FOUND.value();
            throw e;
        } catch (Exception e) {
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
            throw new BadRequestException("Unexpected error occurred: " + e.getMessage());
        }
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<Message> updateRol(@RequestBody @Valid RolDto rolDto, @PathVariable Integer id, HttpServletRequest request) throws BadRequestException {
        RolDto rolUpdate = null;
        try {
            if (!rolService.existById(id)) {
                throw new ResourceNorFoundException(NAME_ENTITY, ID_ENTITY, id.toString());
            }

            rolDto.setRolId(id);

            rolUpdate = rolService.updateRol(id, rolDto, request);

            return new ResponseEntity<>(Message.builder()
                    .note("Update successfully")
                    .object(rolUpdate)
                    .build(), HttpStatus.OK);

        } catch (DataAccessException exDt) {
            throw new BadRequestException("Error updating record: " + exDt.getMessage());
        } catch (Exception e) {
            throw new BadRequestException("Unexpected error occurred: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Message> deleteRol(@PathVariable Integer id, HttpServletRequest request) throws BadRequestException {
        try {
            RolDto rolDelete = rolService.findByIdRol(id, request);
            rolService.deleteRol(rolDelete, request);

            return new ResponseEntity<>(Message.builder()
                    .object(null)
                    .build(),
                    HttpStatus.NO_CONTENT);

        } catch (ResourceNorFoundException e) {
            throw new BadRequestException("Rol not found: " + e.getMessage());
        } catch (DataAccessException e) {
            throw new BadRequestException("Error deleting record: " + e.getMessage());
        } catch (Exception e) {
            throw new BadRequestException("Unexpected error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/show-all")
    public ResponseEntity<MessagePage> showAllRoles(Pageable pageable, HttpServletRequest request){
        try {
            MessagePage message = rolService.listAllRol(pageable, request);
            statusCode = HttpStatus.OK.value();

            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (ResourceNorFoundException e) {
            statusCode = HttpStatus.NOT_FOUND.value();
            auditService.logAudit(null, this.getClass().getMethods()[0], e, statusCode, NAME_ENTITY, request );
            throw e;
        } catch (Exception e) {
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
            auditService.logAudit(null, this.getClass().getMethods()[0], e, statusCode, NAME_ENTITY, request);
            throw new com.is4tech.invoicemanagement.exception.BadRequestException("Unexpected error occurred: " + e.getMessage());
        }
    }

    @PostMapping("/search")
    public ResponseEntity<MessagePage> searchRoles(@RequestBody NameSearchDto roleSearchDto, Pageable pageable, HttpServletRequest request) throws BadRequestException {
        try {
            MessagePage messagePage = rolService.findByNameRol(roleSearchDto, pageable, request);
            statusCode = HttpStatus.OK.value();

            return new ResponseEntity<>(messagePage, HttpStatus.OK);

        } catch (ResourceNorFoundException e) {
            statusCode = HttpStatus.NOT_FOUND.value();
            auditService.logAudit(null, this.getClass().getMethods()[0], e, statusCode, NAME_ENTITY, request );
            throw e;
        } catch (Exception e) {
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
            auditService.logAudit(null, this.getClass().getMethods()[0], e, statusCode, NAME_ENTITY, request);
            throw new BadRequestException("Unexpected error occurred: " + e.getMessage());
        }
    }
}