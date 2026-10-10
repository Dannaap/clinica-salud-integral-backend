package pe.saludintegral.clinica.usuarios;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ActualizarCuentaRequest(@NotBlank String nombres,
                                     @NotBlank String apellidos,
                                     @Email @NotBlank String email) {
}