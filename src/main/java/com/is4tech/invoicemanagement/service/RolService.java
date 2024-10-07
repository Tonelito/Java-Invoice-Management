package com.is4tech.invoicemanagement.service;


import java.util.List;
import java.util.stream.Collectors;

import com.is4tech.invoicemanagement.dto.RoleSearchDto;
import com.is4tech.invoicemanagement.exception.BadRequestException;
import com.is4tech.invoicemanagement.exception.ResourceNorFoundException;
import com.is4tech.invoicemanagement.utils.MessagePage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.is4tech.invoicemanagement.model.Rol;
import com.is4tech.invoicemanagement.dto.RolDto;
import com.is4tech.invoicemanagement.repository.RolRepository;

@Service
public class RolService {
    
    private final RolRepository rolRepository;
    @Autowired
    private AuditService auditService;

    private static final String NAME_ENTITY = "Role";
    private static final String ID_ENTITY = "role_id";

    public RolService(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    @Transactional(readOnly = true)
    public MessagePage listAllRol(Pageable pageable, HttpServletRequest request) {
        Page<Rol> roles = rolRepository.findAll(pageable);

        if (roles.isEmpty()) {
            int statusCode = HttpStatus.NOT_FOUND.value();
            auditService.logAudit(null, this.getClass().getMethods()[0], null, statusCode, "Rol", request);
            throw new ResourceNorFoundException(NAME_ENTITY, ID_ENTITY, pageable.toString());
        }

        int statusCode = HttpStatus.OK.value();
        auditService.logAudit(roles.getContent(), this.getClass().getMethods()[0], null, statusCode, "Rol", request);

        return MessagePage.builder()
                .note("Roles Retrieved Successfully")
                .object(roles.getContent().stream().map(this::toDtoRol).toList())
                .totalElements((int) roles.getTotalElements())
                .totalPages(roles.getTotalPages())
                .currentPage(roles.getNumber())
                .pageSize(roles.getSize())
                .build();
    }
    
    @Transactional
    public RolDto saveRol(RolDto rolDto, HttpServletRequest request) {
        try {
            Rol rol = toModelRol(rolDto);
            Rol savedRol = rolRepository.save(rol);

            int statusCode = HttpStatus.CREATED.value();
            auditService.logAudit(rolDto, this.getClass().getMethods()[0], null, statusCode, NAME_ENTITY, request);

            return toDtoRol(savedRol);
        } catch (Exception e) {
            int statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
            auditService.logAudit(rolDto, this.getClass().getMethods()[0], null, statusCode, NAME_ENTITY, request);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public MessagePage findByNameRol(RoleSearchDto roleSearchDto, Pageable pageable, HttpServletRequest request) {
        if (roleSearchDto == null || roleSearchDto.getName() == null) {
            throw new BadRequestException("Rol name cannot be null or empty");
        }

        String rolName = roleSearchDto.getName();
        Page<Rol> roles = rolRepository.findByNameContainingIgnoreCase(rolName, pageable);

        if (roles.isEmpty()) {
            int statusCode = HttpStatus.NOT_FOUND.value();
            auditService.logAudit(rolName, this.getClass().getMethods()[0], null, statusCode, "Rol", request);
            throw new ResourceNorFoundException("Rol", "Name", rolName);
        }

        int statusCode = HttpStatus.OK.value();
        auditService.logAudit(rolName, this.getClass().getMethods()[0], null, statusCode, "Rol", request);

        List<RolDto> rolDtos = roles.getContent().stream()
                .map(this::toDtoRol)
                .collect(Collectors.toList());

        return MessagePage.builder()
                .note("Roles Found")
                .object(rolDtos)
                .totalElements((int) roles.getTotalElements())
                .totalPages(roles.getTotalPages())
                .currentPage(roles.getNumber())
                .pageSize(roles.getSize())
                .build();
    }

    @Transactional
    public void deleteRol(RolDto rolDto, HttpServletRequest request) {
        try {
            Rol rol = rolRepository.findById(rolDto.getRolId())
                    .orElseThrow(() -> new ResourceNorFoundException("Rol not found"));

            rolRepository.delete(rol);

            int statusCode = HttpStatus.NO_CONTENT.value();
            auditService.logAudit(rolDto, this.getClass().getMethods()[0], null, statusCode, "Rol", request);

        } catch (ResourceNorFoundException e) {
            int statusCode = HttpStatus.NOT_FOUND.value();
            auditService.logAudit(rolDto, this.getClass().getMethods()[0], null, statusCode, "Rol", request);
            throw e;

        } catch (Exception e) {
            int statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
            auditService.logAudit(rolDto, this.getClass().getMethods()[0], null, statusCode, "Rol", request);
            throw new BadRequestException("Unexpected error occurred: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public RolDto findByIdRol(Integer id, HttpServletRequest request) {
        RolDto rolDto = rolRepository.findById(id)
                .map(this::toDtoRol)
                .orElseThrow(() -> new ResourceNorFoundException("Role not found with ID: " + id));

        int statusCode = HttpStatus.OK.value();
        auditService.logAudit(rolDto, this.getClass().getMethods()[0], null, statusCode, NAME_ENTITY, request);

        return rolDto;
    }

    @Transactional
    public RolDto updateRol(Integer id, RolDto rolDto, HttpServletRequest request) {
        if (rolDto.getRolId() == null) {
            throw new BadRequestException("Role ID cannot be null");
        }

        try {
            Rol existingRol = rolRepository.findById(id)
                    .orElseThrow(() -> new ResourceNorFoundException("Role not found"));

            if (rolDto.getName() != null) {
                existingRol.setName(rolDto.getName());
            }
            if (rolDto.getDescription() != null) {
                existingRol.setDescription(rolDto.getDescription());
            }

            Rol savedRol = rolRepository.save(existingRol);

            int statusCode = HttpStatus.OK.value();
            auditService.logAudit(rolDto, this.getClass().getMethods()[0], null, statusCode, "Rol", request);

            return toDtoRol(savedRol);
        } catch (Exception e) {
            int statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
            auditService.logAudit(rolDto, this.getClass().getMethods()[0], e, statusCode, "Rol", request);
            throw new BadRequestException("Error updating role: " + e.getMessage());
        }
    }

    public boolean existById(Integer id){
        return rolRepository.existsById(id);
    }

    private Rol toModelRol(RolDto rolDto) {
        return Rol.builder()
            .rolId(rolDto.getRolId())
            .name(rolDto.getName())
            .description(rolDto.getDescription())
            .status(rolDto.getStatus())
            .build();
    }

    private RolDto toDtoRol(Rol rol) {
        return RolDto.builder()
            .rolId(rol.getRolId())
            .name(rol.getName())
            .description(rol.getDescription())
            .status(rol.getStatus())
            .build();
    }
}
