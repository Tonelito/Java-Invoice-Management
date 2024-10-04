package com.is4tech.invoicemanagement.service;


import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.is4tech.invoicemanagement.model.Rol;
import com.is4tech.invoicemanagement.dto.RolDto;
import com.is4tech.invoicemanagement.repository.RolRepository;

@Service
public class RolService {
    
    //Autowired: Gives us control when injecting our instances
    private final RolRepository rolRepository;

    public RolService(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    //Allows you to prevent the compiler from adding about:
    public List<RolDto> listAllRol(Pageable pageable){
        return rolRepository.findAll(pageable).stream()
            .map(this::toDtoRol)
            .toList();
    }
    
    //Transactional: Supports data transactionality 
    //if you didn't have spring support
    @Transactional
    public RolDto saveRol(RolDto rolDto){
        Rol rol = toModelRol(rolDto);
        return toDtoRol(rolRepository.save(rol));
    }

    //ReadOnly: Commonly used for search or 
    //recovery so that it is read only
    @Transactional(readOnly = true)
    public RolDto findByIdRol(Integer id){
        return rolRepository.findById(id)
        .map(this::toDtoRol)
        .orElseThrow(() -> new RuntimeException("The rol not found"));
    }

    @Transactional
    public void deleteRol(RolDto rolDto){
        rolRepository.delete(toModelRol(rolDto));
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
