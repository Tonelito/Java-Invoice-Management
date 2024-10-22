package com.is4tech.invoicemanagement.utils;

import java.util.Date;

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
  

  public void sendEmailRestorationCode(String destination, String from, String subjet, String codigoRestauraciong) throws MessagingException {
    MimeMessage mimeMessage = mail.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
    helper.setTo(destination);
    helper.setFrom(from);
    helper.setSubject(subjet);
    helper.setText(htmlRestorationCode(codigoRestauraciong), true);
    mail.send(mimeMessage);

    CodeRecoveryDto codeRecoveryDto = CodeRecoveryDto.builder()
      .code(codigoRestauraciong)
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
              "  <title>Sus Credenciales de Acceso - ¡Bienvenido!</title>\n" +
              "  <style>\n" +
              "    @import url('https://fonts.googleapis.com/css2?family=Outfit:wght@300;400;600&display=swap');\n" +
              "    body {\n" +
              "      font-family: 'Outfit', sans-serif;\n" +
              "      line-height: 1.6;\n" +
              "      color: #1f2937;\n" +
              "      background-color: #ffffff;\n" +
              "      margin: 0;\n" +
              "      padding: 20px;\n" +
              "    }\n" +
              "    .container {\n" +
              "      max-width: 600px;\n" +
              "      margin: 0 auto;\n" +
              "      background-color: #ffffff;\n" +
              "      border-radius: 24px;\n" +
              "      overflow: hidden;\n" +
              "      box-shadow: 0 20px 40px rgba(0,0,0,0.1);\n" +
              "      position: relative;\n" +
              "    }\n" +
              "    .container::before {\n" +
              "      content: '';\n" +
              "      position: absolute;\n" +
              "      top: 0;\n" +
              "      left: 0;\n" +
              "      right: 0;\n" +
              "      height: 10px;\n" +
              "      background: linear-gradient(90deg, #4f46e5, #10b981, #3b82f6);\n" +
              "    }\n" +
              "    .header {\n" +
              "      background-color: #4f46e5;\n" +
              "      text-align: center;\n" +
              "      padding: 40px 20px;\n" +
              "      color: #ffffff;\n" +
              "      position: relative;\n" +
              "      overflow: hidden;\n" +
              "    }\n" +
              "    .header h1 {\n" +
              "      margin: 0;\n" +
              "      font-size: 32px;\n" +
              "      font-weight: 600;\n" +
              "      letter-spacing: -0.5px;\n" +
              "    }\n" +
              "    .content {\n" +
              "      padding: 40px;\n" +
              "    }\n" +
              "    .credentials {\n" +
              "      background-color: #f0fdf4;\n" +
              "      border: 2px solid #10b981;\n" +
              "      border-radius: 16px;\n" +
              "      padding: 30px;\n" +
              "      margin-bottom: 30px;\n" +
              "      text-align: center;\n" +
              "      box-shadow: 0 10px 20px rgba(16,185,129,0.1);\n" +
              "    }\n" +
              "    .credentials h2 {\n" +
              "      margin-top: 0;\n" +
              "      font-size: 26px;\n" +
              "      font-weight: 600;\n" +
              "      color: #047857;\n" +
              "    }\n" +
              "    .credentials p {\n" +
              "      margin: 15px 0;\n" +
              "      font-size: 20px;\n" +
              "      color: #1f2937;\n" +
              "    }\n" +
              "    .button {\n" +
              "      display: inline-block;\n" +
              "      background: linear-gradient(90deg, #4f46e5, #3b82f6);\n" +
              "      color: #ffffff;\n" +
              "      text-decoration: none;\n" +
              "      padding: 15px 30px;\n" +
              "      border-radius: 50px;\n" +
              "      font-size: 18px;\n" +
              "      font-weight: 600;\n" +
              "      margin-top: 20px;\n" +
              "      transition: all 0.3s ease;\n" +
              "      box-shadow: 0 5px 15px rgba(79, 70, 229, 0.4);\n" +
              "    }\n" +
              "    .button:hover {\n" +
              "      transform: translateY(-3px);\n" +
              "      box-shadow: 0 10px 20px rgba(79, 70, 229, 0.6);\n" +
              "    }\n" +
              "    .features {\n" +
              "      display: flex;\n" +
              "      justify-content: space-around;\n" +
              "      margin-top: 40px;\n" +
              "      gap: 20px;\n" +
              "    }\n" +
              "    .feature {\n" +
              "      text-align: center;\n" +
              "      flex-basis: 30%;\n" +
              "      padding: 24px;\n" +
              "      background-color: #ffffff;\n" +
              "      border-radius: 16px;\n" +
              "      box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);\n" +
              "    }\n" +
              "    .feature img {\n" +
              "      height: 64px;\n" +
              "      width: 64px;\n" +
              "      margin-bottom: 20px;\n" +
              "    }\n" +
              "    .feature h3 {\n" +
              "      font-size: 1.5rem;\n" +
              "      margin-bottom: 10px;\n" +
              "      color: #6200ea;\n" +
              "    }\n" +
              "    .feature p {\n" +
              "      color: #333;\n" +
              "    }\n" +
              "    .footer {\n" +
              "      background-color: #f9fafb;\n" +
              "      color: #6b7280;\n" +
              "      text-align: center;\n" +
              "      padding: 20px;\n" +
              "      font-size: 14px;\n" +
              "      border-top: 1px solid #e5e7eb;\n" +
              "    }\n" +
              "    .toggle-password {\n" +
              "      cursor: pointer;\n" +
              "    }\n" +
              "  </style>\n" +
              "</head>\n" +
              "<body>\n" +
              "  <div class='container'>\n" +
              "    <div class='header'>\n" +
              "      <h1>¡Bienvenido a Nuestro Sistema de Facturación!</h1>\n" +
              "    </div>\n" +
              "    <div class='content'>\n" +
              "      <p>Estimado usuario,</p>\n" +
              "      <p>Nos complace darle la bienvenida a nuestro sistema de facturación. A continuación, encontrará sus credenciales de acceso:</p>\n" +
              "      <div class='credentials'>\n" +
              "        <h2>Sus Credenciales de Acceso</h2>\n" +
              "        <p><strong>Email:</strong> " + email + "</p>\n" +
              "        <p><strong>Contraseña:</strong> <span id='password' style='color: red; font-size: 30px;'>" + password + "</span>\n" +
              "      </div>\n" +
              "      <center><a href='#' class='button'>¡Acceder al Sistema!</a></center>\n" +
              "    </div>\n" +
              "    <div class='features'>\n" +
              "      <div class='feature'>\n" +
              "        <img src='https://cdn-icons-png.flaticon.com/128/508/508250.png?height=64&width=64' alt='Icono de Facturación Rápida'>\n" +
              "        <h3>Seguridad Garantizada</h3>\n" +
              "        <p>Garantizamos que tus datos estén a salvo.</p>\n" +
              "      </div>\n" +
              "      <div class='feature'>\n" +
              "        <img src='https://cdn-icons-png.flaticon.com/128/3029/3029337.png?height=64&width=64' alt='Icono de Reportes'>\n" +
              "        <h3>Reportes Detallados</h3>\n" +
              "        <p>Obtenga reportes de sus movimientos.</p>\n" +
              "      </div>\n" +
              "      <div class='feature'>\n" +
              "        <img src='https://cdn-icons-png.flaticon.com/128/11494/11494033.png?height=64&width=64' alt='Icono de Soporte'>\n" +
              "        <h3>Soporte 24/7</h3>\n" +
              "        <p>Nuestro equipo está aquí para ayudarle.</p>\n" +
              "      </div>\n" +
              "    </div>\n" +
              "    <div class='footer'>\n" +
              "      <p>© 2024 Todos los derechos reservados.</p>\n" +
              "    </div>\n" +
              "  </div>\n" +
              "</body>\n" +
              "</html>";
  }

  private String htmlRestorationCode(String codigoRestauracion) {
    return "<!DOCTYPE html>\n" +
            "<html lang='es'>\n" +
            "<head>\n" +
            "  <meta charset='UTF-8'>\n" +
            "  <meta name='viewport' content='width=device-width, initial-scale=1.0'>\n" +
            "  <title>Restauración de Contraseña</title>\n" +
            "  <style>\n" +
            "    @import url('https://fonts.googleapis.com/css2?family=Outfit:wght@300;400;600&display=swap');\n" +
            "    body {\n" +
            "      font-family: 'Outfit', sans-serif;\n" +
            "      line-height: 1.6;\n" +
            "      color: #1f2937;\n" +
            "      background-color: #ffffff;\n" +
            "      margin: 0;\n" +
            "      padding: 20px;\n" +
            "    }\n" +
            "    .container {\n" +
            "      max-width: 600px;\n" +
            "      margin: 0 auto;\n" +
            "      background-color: #ffffff;\n" +
            "      border-radius: 24px;\n" +
            "      overflow: hidden;\n" +
            "      box-shadow: 0 20px 40px rgba(0,0,0,0.1);\n" +
            "      position: relative;\n" +
            "    }\n" +
            "    .container::before {\n" +
            "      content: '';\n" +
            "      position: absolute;\n" +
            "      top: 0;\n" +
            "      left: 0;\n" +
            "      right: 0;\n" +
            "      height: 10px;\n" +
            "      background: linear-gradient(90deg, #4f46e5, #10b981, #3b82f6);\n" +
            "    }\n" +
            "    .header {\n" +
            "      background-color: #4f46e5;\n" +
            "      text-align: center;\n" +
            "      padding: 40px 20px;\n" +
            "      color: #ffffff;\n" +
            "      position: relative;\n" +
            "      overflow: hidden;\n" +
            "    }\n" +
            "    .header h1 {\n" +
            "      margin: 0;\n" +
            "      font-size: 32px;\n" +
            "      font-weight: 600;\n" +
            "      letter-spacing: -0.5px;\n" +
            "    }\n" +
            "    .content {\n" +
            "      padding: 40px;\n" +
            "    }\n" +
            "    .credentials {\n" +
            "      background-color: #f0fdf4;\n" +
            "      border: 2px solid #10b981;\n" +
            "      border-radius: 16px;\n" +
            "      padding: 30px;\n" +
            "      margin-bottom: 30px;\n" +
            "      text-align: center;\n" +
            "      box-shadow: 0 10px 20px rgba(16,185,129,0.1);\n" +
            "    }\n" +
            "    .credentials h2 {\n" +
            "      margin-top: 0;\n" +
            "      font-size: 26px;\n" +
            "      font-weight: 600;\n" +
            "      color: #047857;\n" +
            "    }\n" +
            "    .credentials p {\n" +
            "      margin: 15px 0;\n" +
            "      font-size: 20px;\n" +
            "      color: #1f2937;\n" +
            "    }\n" +
            "    .footer {\n" +
            "      background-color: #f9fafb;\n" +
            "      color: #6b7280;\n" +
            "      text-align: center;\n" +
            "      padding: 20px;\n" +
            "      font-size: 14px;\n" +
            "      border-top: 1px solid #e5e7eb;\n" +
            "    }\n" +
            "  </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "  <div class='container'>\n" +
            "    <div class='header'>\n" +
            "      <h1>¡Recupera tu Cuenta!</h1>\n" +
            "    </div>\n" +
            "    <div class='content'>\n" +
            "      <p>Estimado usuario,</p>\n" +
            "      <p>Hemos recibido una solicitud para restablecer su contraseña. Utilice el siguiente código de recuperación:</p>\n" +
            "      <div class='credentials'>\n" +
            "        <h2>Código de Restauración</h2>\n" +
            "        <p><strong>Código:</strong> <span id='codigo' style='color: red; font-size: 30px;'>" + codigoRestauracion + "</span></p>\n" +
            "      </div>\n" +
            "      <p>Por favor, copie el código anterior y regrese a la página para ingresarlo en el apartado donde se solicita.</p>\n" +
            "    </div>\n" +
            "    <div class='footer'>\n" +
            "      <p>Si no solicitó este cambio, puede ignorar este correo.</p>\n" +
            "    </div>\n" +
            "  </div>\n" +
            "</body>\n" +
            "</html>\n";
    }
}

