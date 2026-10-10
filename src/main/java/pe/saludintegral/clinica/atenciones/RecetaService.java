package pe.saludintegral.clinica.atenciones;

import java.util.List;
import java.util.Objects;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import pe.saludintegral.clinica.common.BusinessRuleException;
import pe.saludintegral.clinica.common.ResourceNotFoundException;
import pe.saludintegral.clinica.usuarios.Usuario;

@Service
@RequiredArgsConstructor
@Transactional
public class RecetaService {
    private final RecetaRepository recetaRepository;
    private final AtencionRepository atencionRepository;

    public RecetaResponse crear(Long atencionId, RecetaRequest request, Authentication authentication) {
        Atencion atencion = obtenerAtencion(atencionId);
        validarAcceso(atencion, authentication);
        Receta receta = new Receta(null, atencion, request.medicamento().trim(), request.dosis().trim(),
                request.indicaciones().trim(), request.duracion());
        return RecetaResponse.desde(recetaRepository.save(receta));
    }

    @Transactional(readOnly = true)
    public List<RecetaResponse> listar(Long atencionId, Authentication authentication) {
        validarAcceso(obtenerAtencion(atencionId), authentication);
        return recetaRepository.findByAtencionIdOrderById(atencionId).stream().map(RecetaResponse::desde).toList();
    }

    private Atencion obtenerAtencion(Long id) {
        return atencionRepository.findById(Objects.requireNonNull(id, "atencionId es obligatorio"))
                .orElseThrow(() -> new ResourceNotFoundException("Atención no encontrada"));
    }

    private void validarAcceso(Atencion atencion, Authentication authentication) {
        boolean admin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        Usuario actual = (Usuario) authentication.getPrincipal();
        if (!admin && !atencion.getMedico().getId().equals(actual.getId())) {
            throw new BusinessRuleException("Solo el médico responsable puede consultar o emitir recetas");
        }
    }
}