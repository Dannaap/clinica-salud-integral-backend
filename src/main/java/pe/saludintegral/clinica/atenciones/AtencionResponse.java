package pe.saludintegral.clinica.atenciones;

import java.time.LocalDateTime;

public record AtencionResponse(Long id, Long citaId, Long pacienteId, String paciente,
                               Long medicoId, String medico, String motivoConsulta,
                               String diagnostico, String codigoCie10, String notas, LocalDateTime creadaEn) {
    static AtencionResponse desde(Atencion atencion) {
        var paciente = atencion.getCita().getPaciente();
        return new AtencionResponse(atencion.getId(), atencion.getCita().getId(), paciente.getId(),
                paciente.getNombres() + " " + paciente.getApellidos(), atencion.getMedico().getId(),
                atencion.getMedico().getNombres() + " " + atencion.getMedico().getApellidos(),
                atencion.getMotivoConsulta(), atencion.getDiagnostico(), atencion.getCodigoCie10(),
                atencion.getNotas(), atencion.getCreadaEn());
    }
}