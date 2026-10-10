package pe.saludintegral.clinica.atenciones;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record AtencionRequest(@NotNull Long citaId,
                              @NotBlank String motivoConsulta,
                              @NotBlank String diagnostico,
                              @NotBlank @Pattern(regexp = "^[A-Z][0-9]{2}(\\.[A-Z0-9]{1,4})?$", flags = Pattern.Flag.CASE_INSENSITIVE)
                              String codigoCie10,
                              String notas) {
}