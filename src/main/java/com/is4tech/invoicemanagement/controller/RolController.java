package com.is4tech.invoicemanagement.controller;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Page;
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

import com.is4tech.invoicemanagement.model.Rol;
import com.is4tech.invoicemanagement.dto.RolDto;
import com.is4tech.invoicemanagement.exception.ResourceNorFoundException;
import com.is4tech.invoicemanagement.service.RolService;
import com.is4tech.invoicemanagement.utils.Message;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/invoice-management/v0.1/")
public class RolController {
    
    //Autowired: Gives us control when injecting our instances
    @Autowired  
    private RolService rolService;

    private static final String NAME_ENTITY = "Rol";
    private static final String ID_ENTITY = "Id";

    @PostMapping("/rol")
    public ResponseEntity<Message> saveRol(@RequestBody @Valid RolDto rolDto) throws BadRequestException{
        Rol rolSave = null;
        try {
            rolSave = rolService.saveRol(rolDto);
            return new ResponseEntity<>(Message.builder()
                .note("Saved successfully")
                .object(RolDto.builder()
                        .rolId(rolSave.getRolId())
                        .description(rolSave.getDescription())
                        .name(rolSave.getName())
                        .status(rolSave.getStatus())
                        .build())
                .build(),
                HttpStatus.CREATED);
        } catch (DataAccessException exDt) {
            throw new BadRequestException("Error save record: " + exDt.getMessage());
        }
    }

    @PutMapping("/rol/{id}")
    public ResponseEntity<Message> updateRol(@RequestBody RolDto rolDto,@PathVariable Integer id) throws BadRequestException{
        Rol rolUpdate = null;
        try {
            if(rolService.existById(id)){
                rolDto.setRolId(id);
                rolUpdate = rolService.saveRol(rolDto);
                return new ResponseEntity<>(Message.builder()
                    .note("Update successfully")
                    .object(RolDto.builder()
                            .rolId(rolUpdate.getRolId())
                            .description(rolUpdate.getDescription())
                            .name(rolUpdate.getName())
                            .status(rolUpdate.getStatus())
                            .build())
                    .build(),
                    HttpStatus.OK);
            } else
                throw new ResourceNorFoundException(NAME_ENTITY,ID_ENTITY,id.toString());
            
        } catch (DataAccessException exDt) {
            throw new BadRequestException("Error update record: " + exDt.getMessage());
        }
    }

    @DeleteMapping("/rol/{id}")
    public ResponseEntity<Message> deleteRol(@PathVariable Integer id) throws BadRequestException{
        try {
            Rol rolDelete = rolService.findByIdRol(id); 
            rolService.deleteRol(rolDelete);
            return new ResponseEntity<>(Message.builder()
                                        .object(null)
                                        .build(), 
                                        HttpStatus.NO_CONTENT);
        } catch (DataAccessException e) {
            throw new BadRequestException("Error deleting record: " + e.getMessage());
        }
    }

    @GetMapping("/rol/{id}")
    public ResponseEntity<Message> showByIdRol(@PathVariable Integer id){
        Rol rol = rolService.findByIdRol(id);
        if(rol == null)
            throw new ResourceNorFoundException(NAME_ENTITY,ID_ENTITY,id.toString());

        return new ResponseEntity<>(Message.builder()
                                        .note("Record found")
                                        .object(RolDto.builder()
                                                .rolId(rol.getRolId())
                                                .name(rol.getName())
                                                .description(rol.getDescription())
                                                .status(rol.getStatus())
                                                .build())
                                        .build(),
                                        HttpStatus.NOT_FOUND);
    }

    @GetMapping("/rols")
    public ResponseEntity<Message> showAllRols(@PageableDefault(size = 10) Pageable pageable){
        Page<Rol> rols = rolService.listAllRol(pageable);
        if(rols.isEmpty())
            throw new ResourceNorFoundException(NAME_ENTITY);

        return new ResponseEntity<>(Message.builder()
                                    .note("Records found")
                                    .object(rols.getContent())
                                    .build(),
                                    HttpStatus.OK);
    }
}
