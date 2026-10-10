package pe.saludintegral.clinica.reportes;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {
    private final ReporteService service;

    @GetMapping("/diario/{fecha}/excel")
    public ResponseEntity<byte[]> excel(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha)
            throws IOException {
        return archivo(service.excel(fecha));
    }

    @GetMapping("/diario/{fecha}/pdf")
    public ResponseEntity<byte[]> pdf(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha)
            throws IOException {
        return archivo(service.pdf(fecha));
    }

    private ResponseEntity<byte[]> archivo(ReporteArchivo archivo) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(Objects.requireNonNull(archivo.contentType())))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + archivo.nombre() + "\"")
                .body(archivo.contenido());
    }
}