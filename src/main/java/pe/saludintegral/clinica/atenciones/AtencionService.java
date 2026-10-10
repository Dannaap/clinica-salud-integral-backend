package pe.saludintegral.clinica.atenciones;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import pe.saludintegral.clinica.citas.Cita;
import pe.saludintegral.clinica.citas.CitaRepository;
import pe.saludintegral.clinica.common.BusinessRuleException;
import pe.saludintegral.clinica.common.ResourceNotFoundException;
import pe.saludintegral.clinica.usuarios.Usuario;

@Service
@RequiredArgsConstructor
@Transactional
public class AtencionService {
    private final AtencionRepository atencionRepository;
    private final CitaRepository citaRepository;

    public AtencionResponse registrar(AtencionRequest request, Authentication authentication) {
        Long citaId = Objects.requireNonNull(request.citaId(), "citaId es obligatorio");
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada"));
        Usuario usuario = (Usuario) authentication.getPrincipal();
        boolean admin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        if (!admin && !cita.getTurno().getMedico().getUsuario().getId().equals(usuario.getId())) {
            throw new BusinessRuleException("Solo el médico asignado puede registrar la atención");
        }
        if (cita.getEstado() != Cita.Estado.RESERVADA) {
            throw new BusinessRuleException("La cita no está disponible para registrar una atención");
        }
        if (atencionRepository.existsByCitaId(cita.getId())) {
            throw new BusinessRuleException("La cita ya tiene una atención registrada");
        }
        Atencion atencion = new Atencion(null, cita, usuario, request.motivoConsulta().trim(),
            request.diagnostico().trim(), request.codigoCie10().toUpperCase(), request.notas(), LocalDateTime.now());
        cita.setEstado(Cita.Estado.ATENDIDA);
        citaRepository.save(cita);
        return AtencionResponse.desde(atencionRepository.save(atencion));
    }

    @Transactional(readOnly = true)
    public List<AtencionResponse> listarPaciente(Long pacienteId, Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        boolean admin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        boolean medico = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_MEDICO"));
        List<Atencion> atenciones = medico && !admin
                ? atencionRepository.findByMedicoIdOrderByCreadaEnDesc(usuario.getId()).stream()
                    .filter(atencion -> atencion.getCita().getPaciente().getId().equals(pacienteId)).toList()
                : atencionRepository.findByCitaPacienteIdOrderByCreadaEnDesc(pacienteId);
        return atenciones.stream().map(AtencionResponse::desde).toList();
    }
}