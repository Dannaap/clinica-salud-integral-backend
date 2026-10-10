package pe.saludintegral.clinica.reportes;

import java.time.LocalDate;
import java.util.List;

public record ReporteDiario(LocalDate fecha, long totalCitas, long atendidas, long ausentes,
                            long canceladas, double porcentajeAusentismo, List<FilaAtencion> atenciones) {
    public record FilaAtencion(String paciente, String dni, String medico, String diagnostico,
                               String codigoCie10, String recetaResumen) {
    }
}