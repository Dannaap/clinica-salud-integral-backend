package pe.saludintegral.clinica.reportes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import lombok.RequiredArgsConstructor;
import pe.saludintegral.clinica.atenciones.AtencionRepository;
import pe.saludintegral.clinica.atenciones.RecetaRepository;
import pe.saludintegral.clinica.citas.Cita;
import pe.saludintegral.clinica.citas.CitaRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteService {
    private static final String XLSX_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String PDF_TYPE = "application/pdf";

    private final AtencionRepository atencionRepository;
    private final CitaRepository citaRepository;
    private final RecetaRepository recetaRepository;

    public ReporteDiario obtenerDiario(LocalDate fecha) {
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.plusDays(1).atStartOfDay();
        long atendidas = citaRepository.contarPorEstadoEnRango(Cita.Estado.ATENDIDA, inicio, fin);
        long ausentes = citaRepository.contarPorEstadoEnRango(Cita.Estado.AUSENTE, inicio, fin);
        long canceladas = citaRepository.contarPorEstadoEnRango(Cita.Estado.CANCELADA, inicio, fin);
        long total = atendidas + ausentes + canceladas +
            citaRepository.contarPorEstadoEnRango(Cita.Estado.RESERVADA, inicio, fin);
        double porcentajeAusentismo = atendidas + ausentes == 0 ? 0 : (double) ausentes * 100 / (atendidas + ausentes);

        List<ReporteDiario.FilaAtencion> filas = atencionRepository
                .findByCreadaEnGreaterThanEqualAndCreadaEnLessThanOrderByCreadaEnDesc(inicio, fin).stream()
                .map(atencion -> {
                    var paciente = atencion.getCita().getPaciente();
                    var recetas = recetaRepository.findByAtencionIdOrderById(atencion.getId());
                    String resumen = recetas.stream().map(receta -> receta.getMedicamento() + " (" + receta.getDosis() + ")")
                            .reduce((primera, siguiente) -> primera + "; " + siguiente).orElse("");
                    return new ReporteDiario.FilaAtencion(paciente.getNombres() + " " + paciente.getApellidos(),
                            paciente.getDni(), atencion.getMedico().getNombres() + " " + atencion.getMedico().getApellidos(),
                            atencion.getDiagnostico(), atencion.getCodigoCie10(), resumen);
                }).toList();
        return new ReporteDiario(fecha, total, atendidas, ausentes, canceladas, porcentajeAusentismo, filas);
    }

    public ReporteArchivo excel(LocalDate fecha) throws IOException {
        ReporteDiario reporte = obtenerDiario(fecha);
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            var resumen = workbook.createSheet("Resumen diario");
            String[] encabezados = {"Fecha", "Total citas", "Atendidas", "Ausentes", "Canceladas", "% ausentismo"};
            Row encabezado = resumen.createRow(0);
            for (int indice = 0; indice < encabezados.length; indice++) {
                encabezado.createCell(indice).setCellValue(encabezados[indice]);
            }
            Row datos = resumen.createRow(1);
            datos.createCell(0).setCellValue(fecha.toString());
            datos.createCell(1).setCellValue(reporte.totalCitas());
            datos.createCell(2).setCellValue(reporte.atendidas());
            datos.createCell(3).setCellValue(reporte.ausentes());
            datos.createCell(4).setCellValue(reporte.canceladas());
            datos.createCell(5).setCellValue(reporte.porcentajeAusentismo());
            for (int indice = 0; indice < encabezados.length; indice++) {
                resumen.autoSizeColumn(indice);
            }

            var detalle = workbook.createSheet("Atenciones");
            String[] columnas = {"Paciente", "DNI", "Médico", "Diagnóstico", "CIE-10", "Recetas"};
            Row filaEncabezado = detalle.createRow(0);
            for (int indice = 0; indice < columnas.length; indice++) {
                filaEncabezado.createCell(indice).setCellValue(columnas[indice]);
            }
            for (int fila = 0; fila < reporte.atenciones().size(); fila++) {
                var atencion = reporte.atenciones().get(fila);
                Row registro = detalle.createRow(fila + 1);
                registro.createCell(0).setCellValue(atencion.paciente());
                registro.createCell(1).setCellValue(atencion.dni());
                registro.createCell(2).setCellValue(atencion.medico());
                registro.createCell(3).setCellValue(atencion.diagnostico());
                registro.createCell(4).setCellValue(atencion.codigoCie10());
                registro.createCell(5).setCellValue(atencion.recetaResumen());
            }
            for (int indice = 0; indice < columnas.length; indice++) {
                detalle.autoSizeColumn(indice);
            }
            workbook.write(output);
            return new ReporteArchivo("reporte-clinica-" + fecha + ".xlsx", XLSX_TYPE, output.toByteArray());
        }
    }

    public ReporteArchivo pdf(LocalDate fecha) throws IOException {
        ReporteDiario reporte = obtenerDiario(fecha);
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, output);
            document.open();
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Paragraph titulo = new Paragraph("Reporte operativo diario - " + fecha, titleFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);
            document.add(new Paragraph("Citas: " + reporte.totalCitas()
                    + " | Atendidas: " + reporte.atendidas()
                    + " | Ausentes: " + reporte.ausentes()
                    + " | Canceladas: " + reporte.canceladas()
                    + " | Ausentismo: " + String.format(java.util.Locale.ROOT, "%.2f%%", reporte.porcentajeAusentismo())));
            document.add(new Paragraph(" "));
            PdfPTable tabla = new PdfPTable(new float[]{2.2f, 1.1f, 2f, 2.5f, 0.9f, 2f});
            tabla.setWidthPercentage(100);
            for (String encabezado : List.of("Paciente", "DNI", "Médico", "Diagnóstico", "CIE-10", "Recetas")) {
                tabla.addCell(new Phrase(encabezado, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9)));
            }
            for (var atencion : reporte.atenciones()) {
                tabla.addCell(atencion.paciente());
                tabla.addCell(atencion.dni());
                tabla.addCell(atencion.medico());
                tabla.addCell(atencion.diagnostico());
                tabla.addCell(atencion.codigoCie10());
                tabla.addCell(atencion.recetaResumen());
            }
            document.add(tabla);
            document.close();
            return new ReporteArchivo("reporte-clinica-" + fecha + ".pdf", PDF_TYPE, output.toByteArray());
        }
    }
}