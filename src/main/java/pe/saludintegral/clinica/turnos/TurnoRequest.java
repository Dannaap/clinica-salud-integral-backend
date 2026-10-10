package pe.saludintegral.clinica.turnos;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

public record TurnoRequest(@NotNull Long medicoId, @NotNull LocalDateTime inicio, @NotNull LocalDateTime fin) {
}