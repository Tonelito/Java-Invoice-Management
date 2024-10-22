package com.is4tech.invoicemanagement.service;

import org.springframework.stereotype.Service;

import com.is4tech.invoicemanagement.dto.CodeRecoveryDto;
import com.is4tech.invoicemanagement.model.CodeRecovery;
import com.is4tech.invoicemanagement.repository.CodeRecoveryRepository;

@Service
public class CodeRecoveryService {
    
    private CodeRecoveryRepository codeRecoveryRepository;

    public CodeRecoveryService(CodeRecoveryRepository codeRecoveryRepository) {
        this.codeRecoveryRepository = codeRecoveryRepository;
    }

    public CodeRecoveryDto findByCodeCodeRecovery(String code){
        CodeRecovery codeRecovery = codeRecoveryRepository.findByCode(code);
        if (codeRecovery == null) {
            return null; 
        }
        return toDto(codeRecovery);
    }

    public CodeRecoveryDto saveCodeRecovery(CodeRecoveryDto codeRecoveryDto){
        CodeRecovery codeRecovery = CodeRecovery.builder()
            .codeRecoveryId(codeRecoveryDto.getCodeRecoveryId())
            .code(codeRecoveryDto.getCode())
            .expirationDate(codeRecoveryDto.getExpirationDate())
            .build();
        return this.toDto(codeRecoveryRepository.save(codeRecovery));
    }

    public void deleteCodeRecovery(Integer codeRecoveryId){
        CodeRecovery codeRecoveryDelete = codeRecoveryRepository.findById(codeRecoveryId).orElse(null);
        assert codeRecoveryDelete != null;
        codeRecoveryRepository.delete(codeRecoveryDelete);
    }

    private CodeRecoveryDto toDto(CodeRecovery entity){
        return new CodeRecoveryDto(entity.getCodeRecoveryId(), entity.getCode(), entity.getExpirationDate());
    }
}
