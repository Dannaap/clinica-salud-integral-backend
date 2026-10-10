package pe.saludintegral.clinica.citas;

import jakarta.validation.constraints.NotNull;

public record CitaRequest(@NotNull Long pacienteId, @NotNull Long turnoId, String motivo) {
}