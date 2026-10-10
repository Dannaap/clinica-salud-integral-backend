package pe.saludintegral.clinica.especialidades;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import pe.saludintegral.clinica.common.ApiResponse;

@RestController
@RequestMapping("/api/especialidades")
@RequiredArgsConstructor
public class EspecialidadController {

    private final EspecialidadService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Especialidad>>> listar() {
        return ResponseEntity.ok(ApiResponse.ok(service.listar()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Especialidad>> crear(@RequestBody Especialidad especialidad) {
        return ResponseEntity.ok(ApiResponse.ok(service.crear(especialidad)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Especialidad>> actualizar(@PathVariable Long id,
                                                                 @RequestBody Especialidad especialidad) {
        return ResponseEntity.ok(ApiResponse.ok(service.actualizar(id, especialidad)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}