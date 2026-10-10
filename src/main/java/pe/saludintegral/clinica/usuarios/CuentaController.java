package pe.saludintegral.clinica.usuarios;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import pe.saludintegral.clinica.common.ApiResponse;

@RestController
@RequestMapping("/api/cuenta")
@RequiredArgsConstructor
public class CuentaController {
    private final CuentaService service;

    @GetMapping
    public ResponseEntity<ApiResponse<UsuarioDTO>> obtener(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.ok(service.obtener(authentication)));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<UsuarioDTO>> actualizar(@Valid @RequestBody ActualizarCuentaRequest request,
                                                               Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.ok(service.actualizar(request, authentication)));
    }

    @PutMapping("/contrasena")
    public ResponseEntity<ApiResponse<Void>> cambiarContrasena(@Valid @RequestBody CambiarContrasenaRequest request,
                                                                 Authentication authentication) {
        service.cambiarContrasena(request, authentication);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}