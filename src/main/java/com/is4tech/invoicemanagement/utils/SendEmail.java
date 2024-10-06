package com.is4tech.invoicemanagement.utils;

import java.util.Date;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.is4tech.invoicemanagement.dto.CodePasswordDto;
import com.is4tech.invoicemanagement.dto.CodeRecoveryDto;
import com.is4tech.invoicemanagement.service.CodeRecoveryService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Component
public class SendEmail {
  
  private final JavaMailSender mail;
  private final CodeRecoveryService codeRecoveryService;

  public SendEmail(JavaMailSender mail, CodeRecoveryService codeRecoveryService) {
    this.mail = mail;
    this.codeRecoveryService = codeRecoveryService;
  }

  public void sendEmailPassword(String destination, String from, String subject, String password) throws MessagingException {
      MimeMessage mimeMessage = mail.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
  
      helper.setTo(destination);
      helper.setFrom(from);
      helper.setSubject(subject);
      helper.setText(htmlSend(destination, password), true);
  
      mail.send(mimeMessage);
  }
  

  public void sendEmailRestorationCode(String destination, String from, String subjet, String text, String codigoRestauraciong) {
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

  private String htmlSend(String email, String password) {
    return "<!DOCTYPE html>\n" +
            "<html lang='es'>\n" +
            "<head>\n" +
            "  <meta charset='UTF-8'>\n" +
            "  <meta name='viewport' content='width=device-width, initial-scale=1.0'>\n" +
            "  <title>Credenciales</title>\n" +
            "  <style>\n" +
            "    body {\n" +
            "      font-family: Arial, sans-serif;\n" +
            "      background-color: #f2f2f2;\n" +
            "      color: #333;\n" +
            "      margin: 0;\n" +
            "      padding: 0;\n" +
            "    }\n" +
            "    .email-container {\n" +
            "      display: flex;\n" +
            "      justify-content: center;\n" +
            "      align-items: center;\n" +
            "      min-height: 100vh;\n" +
            "    }\n" +
            "    .card {\n" +
            "      background-color: #ffffff;\n" +
            "      width: 350px;\n" +
            "      border-radius: 10px;\n" +
            "      box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);\n" +
            "      padding: 20px;\n" +
            "      text-align: center;\n" +
            "    }\n" +
            "    .header-triangle {\n" +
            "      width: 0;\n" +
            "      height: 0;\n" +
            "      border-left: 50px solid transparent;\n" +
            "      border-right: 50px solid transparent;\n" +
            "      border-bottom: 30px solid #ff5733;\n" +
            "      margin: 0 auto;\n" +
            "    }\n" +
            "    h1 {\n" +
            "      font-size: 24px;\n" +
            "      margin: 10px 0;\n" +
            "      color: #ff5733;\n" +
            "    }\n" +
            "    p {\n" +
            "      font-size: 16px;\n" +
            "      color: #333;\n" +
            "    }\n" +
            "    hr {\n" +
            "      border: none;\n" +
            "      border-top: 2px solid #ff5733;\n" +
            "      margin: 20px 0;\n" +
            "      width: 80%;\n" +
            "    }\n" +
            "    table {\n" +
            "      width: 100%;\n" +
            "      margin: 20px 0;\n" +
            "      text-align: left;\n" +
            "      color: #333;\n" +
            "    }\n" +
            "    table td {\n" +
            "      padding: 8px 0;\n" +
            "      font-size: 14px;\n" +
            "    }\n" +
            "    a {\n" +
            "      color: #007bff;\n" +
            "      text-decoration: none;\n" +
            "    }\n" +
            "    a:hover {\n" +
            "      text-decoration: underline;\n" +
            "    }\n" +
            "    .footer-triangle {\n" +
            "      width: 0;\n" +
            "      height: 0;\n" +
            "      border-left: 50px solid transparent;\n" +
            "      border-right: 50px solid transparent;\n" +
            "      border-top: 30px solid #ff5733;\n" +
            "      margin: 20px auto 0;\n" +
            "    }\n" +
            "    .footer-text {\n" +
            "      font-size: 12px;\n" +
            "      color: #666;\n" +
            "      margin-top: 10px;\n" +
            "    }\n" +
            "  </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "  <div class='email-container'>\n" +
            "    <div class='card'>\n" +
            "      <div class='header-triangle'></div>\n" +
            "      <h1>CREDENCIALES</h1>\n" +
            "      <p>¡Te damos la bienvenida a Facturación!</p>\n" +
            "      <hr>\n" +
            "      <table>\n" +
            "        <tr>\n" +
            "          <td><strong>Su correo es:</strong></td>\n" +
            "          <td><a href='mailto:" + email + "'>" + email + "</a></td>\n" +
            "        </tr>\n" +
            "        <tr>\n" +
            "          <td><strong>Su contraseña es:</strong></td>\n" +
            "          <td>" + password + "</td>\n" +
            "        </tr>\n" +
            "      </table>\n" +
            "      <p>Si desea cambiar su contraseña, podrá hacerlo en ajustes de la página.</p>\n" +
            "      <div class='footer-triangle'></div>\n" +
            "      <p class='footer-text'>&copy; 2024 Facturación. Todos los derechos reservados.</p>\n" +
            "    </div>\n" +
            "  </div>\n" +
            "</body>\n" +
            "</html>";
}

}

