package pe.saludintegral.clinica.pacientes;

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
@RequestMapping("/api/pacientes")
@RequiredArgsConstructor
public class PacienteController {

    private final PacienteService pacienteService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Paciente>>> listar() {
        return ResponseEntity.ok(ApiResponse.ok(pacienteService.listarTodos()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Paciente>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(pacienteService.buscarPorId(id)));
    }

    @GetMapping("/dni/{dni}")
    public ResponseEntity<ApiResponse<Paciente>> buscarPorDni(@PathVariable String dni) {
        return ResponseEntity.ok(ApiResponse.ok(pacienteService.buscarPorDni(dni)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Paciente>> crear(@Valid @RequestBody Paciente paciente) {
        return ResponseEntity.ok(ApiResponse.ok(pacienteService.crear(paciente)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Paciente>> actualizar(@PathVariable Long id, @Valid @RequestBody Paciente paciente) {
        return ResponseEntity.ok(ApiResponse.ok(pacienteService.actualizar(id, paciente)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        pacienteService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}