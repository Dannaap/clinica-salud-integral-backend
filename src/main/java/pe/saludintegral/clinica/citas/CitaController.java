package pe.saludintegral.clinica.citas;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
@RequestMapping("/api/citas")
@RequiredArgsConstructor
public class CitaController {
    private final CitaService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CitaResponse>>> listar(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.ok(service.listar(authentication)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CitaResponse>> reservar(@Valid @RequestBody CitaRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.reservar(request)));
    }

    @PutMapping("/{id}/reprogramar")
    public ResponseEntity<ApiResponse<CitaResponse>> reprogramar(@PathVariable Long id,
                                                                  @Valid @RequestBody ReprogramarCitaRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.reprogramar(id, request.turnoId())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<CitaResponse>> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.cancelar(id)));
    }
}