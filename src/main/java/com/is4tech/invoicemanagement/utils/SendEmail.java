package com.is4tech.invoicemanagement.utils;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.is4tech.invoicemanagement.dto.CodePasswordDto;
import com.is4tech.invoicemanagement.dto.CodeRecoveryDto;
import com.is4tech.invoicemanagement.service.AuthService;
import com.is4tech.invoicemanagement.service.CodeRecoveryService;

@Component
public class SendEmail {
  
  @Autowired
  private JavaMailSender mail;

  @Autowired
  private CodeRecoveryService codeRecoveryService;

  public void sendEmailPassword(String destination, String from, String subjet, String text){
    SimpleMailMessage email = new SimpleMailMessage();
    email.setTo(destination);
    email.setFrom(from);
    email.setSubject(subjet);
    email.setText(text);
    mail.send(email);
  }

  public void sendEmailRestorationCode(String destination, String from, String subjet, String text, String codigoRestauracion, String email) {
    SimpleMailMessage emailMessage = new SimpleMailMessage();
    emailMessage.setTo(destination);
    emailMessage.setFrom(from);
    emailMessage.setSubject(subjet);
    emailMessage.setText(text);
    mail.send(emailMessage);

    CodeRecoveryDto codeRecoveryDto = CodeRecoveryDto.builder()
      .code(codigoRestauracion)
      .expirationDate(new Date())
      .build();

    codeRecoveryService.saveCodeRecovery(codeRecoveryDto);
  }

  public String verificCode(CodePasswordDto codePasswordDto) {
    String code = codePasswordDto.getCode();
    CodeRecoveryDto codeRecoveryDto = codeRecoveryService.findByCodeCodeRecovery(code);
    if (codeRecoveryDto == null) {
        return "The code does not exist / not valid";
    }
    
    Date dateCurrent = new Date();
    Date expirationDate = codeRecoveryDto.getExpirationDate();
    long differenceInMillis = dateCurrent.getTime() - expirationDate.getTime();
    long differenceInHours = differenceInMillis / (1000 * 60 * 60);

    if (differenceInHours < 2) {
        if (code.equals(codeRecoveryDto.getCode())) {
            codeRecoveryService.deleteCodeRecovery(codeRecoveryDto.getCodeRecoveryId());
            return "Code valid";
        } else {
            return "The code is invalid";
        }
    } else {
        return "The code expired";
    }
  }
}
