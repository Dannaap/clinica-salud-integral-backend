package pe.saludintegral.clinica.citas;

import java.time.LocalDateTime;

public record CitaResponse(Long id, Long pacienteId, String paciente, String dniPaciente,
                           Long medicoId, String medico, String especialidad,
                           LocalDateTime inicio, LocalDateTime fin, Cita.Estado estado, String motivo) {
    static CitaResponse desde(Cita cita) {
        var usuario = cita.getTurno().getMedico().getUsuario();
        return new CitaResponse(cita.getId(), cita.getPaciente().getId(),
                cita.getPaciente().getNombres() + " " + cita.getPaciente().getApellidos(), cita.getPaciente().getDni(),
                cita.getTurno().getMedico().getId(), usuario.getNombres() + " " + usuario.getApellidos(),
                cita.getTurno().getMedico().getEspecialidad().getNombre(), cita.getTurno().getInicio(),
                cita.getTurno().getFin(), cita.getEstado(), cita.getMotivo());
    }
}