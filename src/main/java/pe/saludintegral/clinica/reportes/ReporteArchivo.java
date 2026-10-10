package pe.saludintegral.clinica.reportes;

public record ReporteArchivo(String nombre, String contentType, byte[] contenido) {
}