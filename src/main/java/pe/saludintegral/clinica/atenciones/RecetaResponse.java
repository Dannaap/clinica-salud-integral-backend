package pe.saludintegral.clinica.atenciones;

public record RecetaResponse(Long id, Long atencionId, String medicamento,
                             String dosis, String indicaciones, String duracion) {
    static RecetaResponse desde(Receta receta) {
        return new RecetaResponse(receta.getId(), receta.getAtencion().getId(), receta.getMedicamento(),
                receta.getDosis(), receta.getIndicaciones(), receta.getDuracion());
    }
}