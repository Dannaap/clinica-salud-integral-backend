package pe.saludintegral.clinica.notificaciones;

import java.time.LocalDateTime;

public record NotificacionResponse(Long id, Long citaId, String paciente, String destinatario,
                                   Notificacion.Tipo tipo, String mensaje,
                                   Notificacion.EstadoEntrega estadoEntrega, LocalDateTime creadaEn) {
    static NotificacionResponse desde(Notificacion notificacion) {
        var paciente = notificacion.getCita().getPaciente();
        return new NotificacionResponse(notificacion.getId(), notificacion.getCita().getId(),
                paciente.getNombres() + " " + paciente.getApellidos(), notificacion.getDestinatario(),
                notificacion.getTipo(), notificacion.getMensaje(), notificacion.getEstadoEntrega(),
                notificacion.getCreadaEn());
    }
}