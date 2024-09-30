package com.is4tech.invoicemanagement.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.is4tech.invoicemanagement.model.Rol;
import com.is4tech.invoicemanagement.dto.RolDto;
import com.is4tech.invoicemanagement.repository.RolRepository;

@Service
public class RolService {
    
    //Autowired: Gives us control when injecting our instances
    @Autowired
    private final RolRepository rolRepository;

    public RolService(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    //Allows you to prevent the compiler from adding about:
    public Page<Rol> listAllRol(Pageable pageable){
        return rolRepository.findAll(pageable);
    }
    
    //Transactional: Supports data transactionality 
    //if you didn't have spring support
    @Transactional
    public Rol saveRol(RolDto rolDto){
        Rol rol = Rol.builder()
                    .rolId(rolDto.getRolId())
                    .name(rolDto.getName())
                    .description(rolDto.getDescription())
                    .status(rolDto.getStatus())
                    .build();
        return rolRepository.save(rol);
    }

    //ReadOnly: Commonly used for search or 
    //recovery so that it is read only
    @Transactional(readOnly = true)
    public Rol findByIdRol(Integer id){
        return rolRepository.findById(id).orElse(null);
    }

    @Transactional
    public void deleteRol(Rol rol){
        rolRepository.delete(rol);
    }

    public boolean existById(Integer id){
        return rolRepository.existsById(id);
    }

}
