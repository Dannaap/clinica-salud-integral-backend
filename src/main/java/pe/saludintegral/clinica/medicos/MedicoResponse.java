package pe.saludintegral.clinica.medicos;

public record MedicoResponse(
        Long id,
        Long usuarioId,
        String nombres,
        String apellidos,
        String email,
        Long especialidadId,
        String especialidad,
        String colegiatura,
        boolean activo
) {
    static MedicoResponse desde(Medico medico) {
        return new MedicoResponse(medico.getId(), medico.getUsuario().getId(), medico.getUsuario().getNombres(),
                medico.getUsuario().getApellidos(), medico.getUsuario().getEmail(), medico.getEspecialidad().getId(),
                medico.getEspecialidad().getNombre(), medico.getColegiatura(), medico.isActivo());
    }
}