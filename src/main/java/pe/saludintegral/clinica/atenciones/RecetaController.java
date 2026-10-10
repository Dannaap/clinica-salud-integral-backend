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
@RequestMapping("/api/atenciones/{atencionId}/recetas")
@RequiredArgsConstructor
public class RecetaController {
    private final RecetaService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RecetaResponse>>> listar(@PathVariable Long atencionId,
                                                                     Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.ok(service.listar(atencionId, authentication)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RecetaResponse>> crear(@PathVariable Long atencionId,
                                                              @Valid @RequestBody RecetaRequest request,
                                                              Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.ok(service.crear(atencionId, request, authentication)));
    }
}