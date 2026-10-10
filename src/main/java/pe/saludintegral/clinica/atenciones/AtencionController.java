package pe.saludintegral.clinica.atenciones;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import pe.saludintegral.clinica.common.ApiResponse;

@RestController
@RequestMapping("/api/atenciones")
@RequiredArgsConstructor
public class AtencionController {
    private final AtencionService service;

    @PostMapping
    public ResponseEntity<ApiResponse<AtencionResponse>> registrar(@Valid @RequestBody AtencionRequest request,
                                                                    Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.ok(service.registrar(request, authentication)));
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<ApiResponse<List<AtencionResponse>>> listarPaciente(@PathVariable Long pacienteId,
                                                                                Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.ok(service.listarPaciente(pacienteId, authentication)));
    }
}