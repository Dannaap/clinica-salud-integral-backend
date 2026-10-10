package pe.saludintegral.clinica.atenciones;

import jakarta.validation.constraints.NotBlank;

public record RecetaRequest(@NotBlank String medicamento, @NotBlank String dosis,
                            @NotBlank String indicaciones, String duracion) {
}