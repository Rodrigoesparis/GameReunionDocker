package com.rodrigo.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationCode(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@gamereunion.com");
        message.setTo(toEmail);
        message.setSubject("GameReunion - Verifica tu cuenta");
        message.setText(
            "¡Bienvenido a GameReunion!\n\n" +
            "Tu código de verificación es: " + code + "\n\n" +
            "Introdúcelo en la app para activar tu cuenta.\n" +
            "El código expira en 15 minutos.\n\n" +
            "Si no has creado esta cuenta, ignora este mensaje."
        );
        mailSender.send(message);
    }
}
