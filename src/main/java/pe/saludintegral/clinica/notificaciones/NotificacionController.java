package pe.saludintegral.clinica.notificaciones;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import pe.saludintegral.clinica.common.ApiResponse;

@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {
    private final NotificacionService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificacionResponse>>> listar() {
        return ResponseEntity.ok(ApiResponse.ok(service.listar()));
    }
}