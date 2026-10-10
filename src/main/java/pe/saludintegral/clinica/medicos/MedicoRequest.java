package pe.saludintegral.clinica.medicos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MedicoRequest(
        @NotNull Long usuarioId,
        @NotNull Long especialidadId,
        @NotBlank String colegiatura
) {
}