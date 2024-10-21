package com.is4tech.invoicemanagement.controller;

import com.is4tech.invoicemanagement.dto.NameSearchDto;
import com.is4tech.invoicemanagement.dto.RolDto;
import com.is4tech.invoicemanagement.exception.ResourceNorFoundException;
import com.is4tech.invoicemanagement.service.AuditService;
import com.is4tech.invoicemanagement.service.RolService;
import com.is4tech.invoicemanagement.utils.Message;
import com.is4tech.invoicemanagement.utils.MessagePage;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class RolControllerTest {

    @Mock
    private AuditService auditService;
    
    @Mock
    private RolService rolService;

    @Mock 
    private HttpServletRequest request;

    @InjectMocks
    private RolController rolController;

    private RolDto rolDto;
    private int id = 1;

    private static final String NAME_ENTITY = "Role";
    private static final String ID_ENTITY = "role_id";
    private static final String UNEXPEDTED_ERROR = "Unexpected error occurred: Unexpected error";
    private static final String NOT_FOUND = "No se encontraron registros de Role en el sistema";
    private static final String MESSAJE_UNEXPEDTED = "Unexpected error";
    private static final String MESSAJE_DB_ERROR = "DB error";
    private static final String MESSAJE_NOT_FOUND = "No Encontrado en el sistema en el sistema";

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        rolDto = RolDto.builder()
            .rolId(id)
            .name("ADMIN")
            .build();
    }

    @Test
    void saveRol() throws Exception {
        when(rolService.saveRol(any(RolDto.class), any(HttpServletRequest.class))).thenReturn(rolDto);

        ResponseEntity<Message> response = rolController.saveRol(rolDto, request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Saved successfully", response.getBody().getNote());
        verify(rolService, times(id)).saveRol(any(RolDto.class), any(HttpServletRequest.class));
    }


    @Test
    void saveRolDataAccessException() {

        when(rolService.saveRol(any(RolDto.class), any(HttpServletRequest.class)))
            .thenThrow(new DataAccessException(MESSAJE_DB_ERROR) {});

        BadRequestException thrown = assertThrows(BadRequestException.class, () -> 
            rolController.saveRol(rolDto, request)
        );

        assertEquals("Error saving record: DB error", thrown.getMessage());
    }

    @Test
    void saveRolResourceNorFoundException() {

        when(rolService.saveRol(any(RolDto.class), any(HttpServletRequest.class)))
            .thenThrow(new ResourceNorFoundException(MESSAJE_NOT_FOUND));

        ResourceNorFoundException thrown = assertThrows(ResourceNorFoundException.class, () -> 
            rolController.saveRol(rolDto, request)
        );

        assertEquals(NOT_FOUND, thrown.getMessage());
    }

    @Test
    void saveRolUnhandledException() {

        when(rolService.saveRol(any(RolDto.class), any(HttpServletRequest.class)))
            .thenThrow(new RuntimeException(MESSAJE_UNEXPEDTED));

        BadRequestException thrown = assertThrows(BadRequestException.class, () -> 
            rolController.saveRol(rolDto, request)
        );

        assertEquals(UNEXPEDTED_ERROR, thrown.getMessage());
    }
    
    @Test
    void updateRol() throws BadRequestException {

        when(rolService.existById(id)).thenReturn(true);
        when(rolService.updateRol(eq(id),any(RolDto.class), any(HttpServletRequest.class))).thenReturn(rolDto);

        ResponseEntity<Message> response = rolController.updateRol(rolDto, id, request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Update successfully", response.getBody().getNote());
        verify(rolService, times(id)).updateRol(eq(id), any(RolDto.class), any(HttpServletRequest.class));
    }

    @Test
    void updateRolFalse(){

        when(rolService.existById(id)).thenReturn(false);
        
        BadRequestException thrown = assertThrows(
            BadRequestException.class,
            () -> rolController.updateRol(rolDto, id, request)
        );

        assertEquals(String.format("Unexpected error occurred: %s was not found with: %s = '%s'", NAME_ENTITY, ID_ENTITY, id), 
            thrown.getMessage());
        verify(rolService, times(id)).existById(id);
        verify(rolService, never()).updateRol(anyInt(), any(RolDto.class), any(HttpServletRequest.class));
    }

    @Test
    void updateRolDataAccessException() {

        when(rolService.existById(id)).thenReturn(true);
        
        when(rolService.updateRol(eq(id), any(RolDto.class), any(HttpServletRequest.class)))
            .thenThrow(new DataAccessException(MESSAJE_DB_ERROR) {});

        BadRequestException thrown = assertThrows(BadRequestException.class, () -> 
            rolController.updateRol(rolDto, id, request)
        );

        assertEquals("Error updating record: DB error",  thrown.getMessage());

        verify(rolService, times(id)).updateRol(eq(id), any(RolDto.class), any(HttpServletRequest.class));
    }
    
    @Test
    void deleteRol() throws BadRequestException {

        when(rolService.findByIdRol(id)).thenReturn(rolDto);
        doNothing().when(rolService).deleteRol(any(RolDto.class), any(HttpServletRequest.class));

        ResponseEntity<Message> response = rolController.deleteRol(id, request);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(rolService, times(id)).deleteRol(any(RolDto.class), any(HttpServletRequest.class));
    }

    @Test
    void deleteRolResourceNorFoundException() {

        when(rolService.findByIdRol(id))
            .thenThrow(new ResourceNorFoundException(MESSAJE_NOT_FOUND));
        
        doThrow(new ResourceNorFoundException(MESSAJE_NOT_FOUND) {})
            .when(rolService).deleteRol(any(RolDto.class), any(HttpServletRequest.class));

        ResourceNorFoundException thrown = assertThrows(ResourceNorFoundException.class, () -> 
            rolController.deleteRol(id, request)
        );

        assertEquals(String.format("%s was not found with: %s = '%s'", NAME_ENTITY, ID_ENTITY, id),
            thrown.getMessage());
    }

    @Test
    void deleteRolDataAccessException() {
        
        when(rolService.findByIdRol(id)).thenReturn(rolDto);
        
        doThrow(new DataAccessException(MESSAJE_DB_ERROR) {})
            .when(rolService).deleteRol(any(RolDto.class), any(HttpServletRequest.class));

        BadRequestException thrown = assertThrows(BadRequestException.class, () -> 
            rolController.deleteRol(id, request)
        );

        assertEquals("Error updating record: DB error", thrown.getMessage());

        verify(rolService, times(id)).deleteRol(any(RolDto.class), any(HttpServletRequest.class));
    }

    @Test
    void deleteRolException() {
        
        when(rolService.findByIdRol(id)).thenReturn(rolDto);

        doThrow(new RuntimeException(MESSAJE_UNEXPEDTED))
            .when(rolService).deleteRol(any(RolDto.class), any(HttpServletRequest.class));

        BadRequestException thrown = assertThrows(BadRequestException.class, () -> 
            rolController.deleteRol(id, request)
        );

        assertEquals(UNEXPEDTED_ERROR, thrown.getMessage());

        verify(rolService, times(id)).deleteRol(any(RolDto.class), any(HttpServletRequest.class));
    }


    @Test
    void showAllRoles() throws BadRequestException {
        Pageable pageable = PageRequest.of(0, 1);
        MessagePage expectedMessagePage = new MessagePage();

        when(rolService.listAllRol(pageable)).thenReturn(expectedMessagePage);

        ResponseEntity<MessagePage> response = rolController.showAllRoles(pageable, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedMessagePage, response.getBody());
        verify(rolService, times(id)).listAllRol(pageable);
    }

    @Test
    void showAllRolesResourceNorFoundException() {
        Pageable pageable = PageRequest.of(0, 1);

        when(rolService.listAllRol(pageable))
            .thenThrow(new ResourceNorFoundException(MESSAJE_NOT_FOUND));

        ResourceNorFoundException thrown = assertThrows(ResourceNorFoundException.class, () -> 
            rolController.showAllRoles(pageable, request)
        );
        
        assertEquals(NOT_FOUND, thrown.getMessage());
        verify(rolService, times(id)).listAllRol(pageable);
    }

    @Test
    void showAllRolesException() {
        Pageable pageable = PageRequest.of(0, 10);

        when(rolService.listAllRol(pageable))
            .thenThrow(new RuntimeException(MESSAJE_UNEXPEDTED));

        BadRequestException thrown = assertThrows(BadRequestException.class, () -> 
            rolController.showAllRoles(pageable, request)
        );
        
        assertEquals(UNEXPEDTED_ERROR, thrown.getMessage());
        verify(rolService, times(id)).listAllRol(pageable);
    }

    @Test
    void searchRoles() throws Exception {
        NameSearchDto roleSearchDto = NameSearchDto.builder().name("admin").build();

        Pageable pageable = PageRequest.of(0, 10);
        MessagePage expectedMessagePage = new MessagePage();

        when(rolService.findByNameRol(roleSearchDto, pageable)).thenReturn(expectedMessagePage);

        ResponseEntity<MessagePage> response = rolController.searchRoles(roleSearchDto, pageable, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedMessagePage, response.getBody());
        verify(rolService, times(1)).findByNameRol(roleSearchDto, pageable);
    }

    @Test
    void searchRolesResourceNorFoundException() {
        NameSearchDto roleSearchDto = NameSearchDto.builder().name("admin").build();

        Pageable pageable = PageRequest.of(0, 10);

        when(rolService.findByNameRol(roleSearchDto, pageable))
            .thenThrow(new ResourceNorFoundException(MESSAJE_NOT_FOUND));

        ResourceNorFoundException thrown = assertThrows(ResourceNorFoundException.class, () -> 
            rolController.searchRoles(roleSearchDto, pageable, request)
        );
        
        assertEquals(NOT_FOUND, thrown.getMessage());
        verify(rolService, times(1)).findByNameRol(roleSearchDto, pageable);
    }

    @Test
    void searchRolesException() {
        NameSearchDto roleSearchDto = NameSearchDto.builder().name("admin").build();

        Pageable pageable = PageRequest.of(0, 10);

        when(rolService.findByNameRol(roleSearchDto, pageable))
            .thenThrow(new RuntimeException(MESSAJE_UNEXPEDTED));

        BadRequestException thrown = assertThrows(BadRequestException.class, () -> 
            rolController.searchRoles(roleSearchDto, pageable, request)
        );
        
        assertEquals(UNEXPEDTED_ERROR, thrown.getMessage());
        verify(rolService, times(1)).findByNameRol(roleSearchDto, pageable);
    }
}
