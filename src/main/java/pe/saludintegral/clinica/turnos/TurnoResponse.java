package pe.saludintegral.clinica.turnos;

import java.time.LocalDateTime;

public record TurnoResponse(Long id, Long medicoId, String medico, String especialidad,
                            LocalDateTime inicio, LocalDateTime fin, boolean disponible) {
    static TurnoResponse desde(Turno turno, boolean disponible) {
        return new TurnoResponse(turno.getId(), turno.getMedico().getId(),
                turno.getMedico().getUsuario().getNombres() + " " + turno.getMedico().getUsuario().getApellidos(),
                turno.getMedico().getEspecialidad().getNombre(), turno.getInicio(), turno.getFin(), disponible);
    }
}