package pe.saludintegral.clinica.medicos;

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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import pe.saludintegral.clinica.common.ApiResponse;

@RestController
@RequestMapping("/api/medicos")
@RequiredArgsConstructor
public class MedicoController {

    private final MedicoService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MedicoResponse>>> listar() {
        return ResponseEntity.ok(ApiResponse.ok(service.listar()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MedicoResponse>> crear(@Valid @RequestBody MedicoRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.crear(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MedicoResponse>> actualizar(@PathVariable Long id,
                                                                   @Valid @RequestBody MedicoRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}