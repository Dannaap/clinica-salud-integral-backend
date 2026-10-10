package pe.saludintegral.clinica.notificaciones;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "app.notifications.email", name = "enabled", havingValue = "true")
public class EmailNotificationSender {
    private final JavaMailSender mailSender;
    private final String from;

    public EmailNotificationSender(JavaMailSender mailSender,
                                   @Value("${app.notifications.email.from:no-reply@clinica.test}") String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    public void enviar(Notificacion notificacion) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(notificacion.getDestinatario());
        message.setSubject("Clínica Salud Integral: " + notificacion.getTipo().name());
        message.setText(notificacion.getMensaje());
        mailSender.send(message);
    }
}