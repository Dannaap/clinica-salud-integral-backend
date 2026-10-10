package pe.saludintegral.clinica.citas;

import jakarta.validation.constraints.NotNull;

public record ReprogramarCitaRequest(@NotNull Long turnoId) {
}