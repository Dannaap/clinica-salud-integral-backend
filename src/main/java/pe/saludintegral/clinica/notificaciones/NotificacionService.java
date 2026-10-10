package pe.saludintegral.clinica.notificaciones;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import pe.saludintegral.clinica.citas.Cita;
import pe.saludintegral.clinica.citas.CitaRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificacionService {
    private final NotificacionRepository notificacionRepository;
    private final CitaRepository citaRepository;
    private final ObjectProvider<EmailNotificationSender> emailSender;

    public void confirmar(Cita cita) {
        crearSiNueva(cita, Notificacion.Tipo.CONFIRMACION,
                "Su cita quedó confirmada para " + cita.getTurno().getInicio());
    }

    public void reprogramar(Cita cita) {
        crearSiNueva(cita, Notificacion.Tipo.REPROGRAMACION,
                "Su cita fue reprogramada para " + cita.getTurno().getInicio());
    }

    public void cancelar(Cita cita) {
        crearSiNueva(cita, Notificacion.Tipo.CANCELACION,
                "Su cita del " + cita.getTurno().getInicio() + " fue cancelada");
    }

    @Transactional(readOnly = true)
    public List<NotificacionResponse> listar() {
        return notificacionRepository.findAllByOrderByCreadaEnDesc().stream()
                .map(NotificacionResponse::desde).toList();
    }

    @Scheduled(fixedDelayString = "${app.notifications.reminder-scan-ms:60000}")
    public void generarRecordatorios() {
        LocalDateTime ahora = LocalDateTime.now();
        List<Cita> citas = citaRepository.findByEstadoAndTurno_InicioBetween(
                Cita.Estado.RESERVADA, ahora.plusHours(23), ahora.plusHours(24));
        citas.forEach(cita -> crearSiNueva(cita, Notificacion.Tipo.RECORDATORIO,
                "Recordatorio: tiene una cita mañana a las " + cita.getTurno().getInicio()));
    }

        @Scheduled(fixedDelayString = "${app.notifications.no-show-scan-ms:60000}")
        public void marcarCitasAusentes() {
        List<Cita> expiradas = citaRepository.findByEstadoAndTurno_FinBefore(
            Cita.Estado.RESERVADA, LocalDateTime.now());
        expiradas.forEach(cita -> cita.setEstado(Cita.Estado.AUSENTE));
        citaRepository.saveAll(expiradas);
        }

    private void crearSiNueva(Cita cita, Notificacion.Tipo tipo, String mensaje) {
        if (notificacionRepository.existsByCitaIdAndTipo(cita.getId(), tipo)) {
            return;
        }
        String email = cita.getPaciente().getEmail();
        String destinatario = email == null || email.isBlank() ? "Paciente " + cita.getPaciente().getDni() : email;
        Notificacion notificacion = new Notificacion(null, cita, tipo, destinatario, mensaje,
                email == null || email.isBlank() ? Notificacion.EstadoEntrega.SIN_CORREO
                        : Notificacion.EstadoEntrega.REGISTRADA,
                LocalDateTime.now());
        Notificacion registrada = notificacionRepository.save(notificacion);
        if (email != null && !email.isBlank()) {
            emailSender.ifAvailable(sender -> {
                try {
                    sender.enviar(registrada);
                    registrada.setEstadoEntrega(Notificacion.EstadoEntrega.ENVIADA);
                } catch (org.springframework.mail.MailException exception) {
                    registrada.setEstadoEntrega(Notificacion.EstadoEntrega.ERROR);
                }
                notificacionRepository.save(registrada);
            });
        }
    }
}