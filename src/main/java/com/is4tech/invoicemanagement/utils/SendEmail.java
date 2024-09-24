package com.is4tech.invoicemanagement.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/test/v1")
@RestController
public class SendEmail {
  
  @Autowired
  private JavaMailSender mail;

  @PostMapping("/sendEmail")
  public ResponseEntity<Boolean> sendEmail(@RequestBody String destination){
    SimpleMailMessage envio = configurationEmail(destination);
    mail.send(envio);
    return new ResponseEntity<>(true, HttpStatus.OK);
  }

  private SimpleMailMessage configurationEmail(String destination){
    
    SimpleMailMessage email = new SimpleMailMessage();
    String codigoRestauracion = ResetCodeGenerator.generateResetCode(10);

    email.setTo(destination);
    email.setFrom("facturacion757@gmail.com");
    email.setSubject("Recuperacion de Contraseña");
    email.setText("Tu codigo de recuperacion es: " + codigoRestauracion);

    return email;
  }
}
