package pe.saludintegral.clinica.citas;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import pe.saludintegral.clinica.common.BusinessRuleException;
import pe.saludintegral.clinica.common.ResourceNotFoundException;
import pe.saludintegral.clinica.notificaciones.NotificacionService;
import pe.saludintegral.clinica.pacientes.PacienteRepository;
import pe.saludintegral.clinica.turnos.Turno;
import pe.saludintegral.clinica.turnos.TurnoRepository;
import pe.saludintegral.clinica.usuarios.Usuario;

@Service
@RequiredArgsConstructor
@Transactional
public class CitaService {
    private final CitaRepository citaRepository;
    private final TurnoRepository turnoRepository;
    private final PacienteRepository pacienteRepository;
    private final NotificacionService notificacionService;

    @Transactional(readOnly = true)
    public List<CitaResponse> listar(Authentication authentication) {
        boolean medico = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_MEDICO"));
        List<Cita> citas = medico
                ? citaRepository.findByTurnoMedicoUsuarioIdOrderByTurnoInicio(
                        ((Usuario) authentication.getPrincipal()).getId())
                : citaRepository.findAll();
        return citas.stream().map(CitaResponse::desde).toList();
    }

    public CitaResponse reservar(CitaRequest request) {
        Turno turno = turnoRepository.findByIdForUpdate(request.turnoId())
                .orElseThrow(() -> new ResourceNotFoundException("Turno no encontrado"));
        validarReservable(turno);
        if (citaRepository.existsByTurnoIdAndEstado(turno.getId(), Cita.Estado.RESERVADA)) {
            throw new BusinessRuleException("El turno ya está reservado");
        }
        var paciente = pacienteRepository.findById(request.pacienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado"));
        Cita cita = new Cita(null, paciente, turno, Cita.Estado.RESERVADA, request.motivo(), LocalDateTime.now());
        cita = citaRepository.save(cita);
        notificacionService.confirmar(cita);
        return CitaResponse.desde(cita);
    }

    public CitaResponse reprogramar(Long id, Long turnoId) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada"));
        if (cita.getEstado() != Cita.Estado.RESERVADA) {
            throw new BusinessRuleException("Solo se pueden reprogramar citas reservadas");
        }
        Turno nuevoTurno = turnoRepository.findByIdForUpdate(turnoId)
                .orElseThrow(() -> new ResourceNotFoundException("Turno no encontrado"));
        validarReservable(nuevoTurno);
        if (citaRepository.existsByTurnoIdAndEstado(turnoId, Cita.Estado.RESERVADA)) {
            throw new BusinessRuleException("El turno ya está reservado");
        }
        cita.setTurno(nuevoTurno);
        cita = citaRepository.save(cita);
        notificacionService.reprogramar(cita);
        return CitaResponse.desde(cita);
    }

    public CitaResponse cancelar(Long id) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada"));
        if (cita.getEstado() != Cita.Estado.RESERVADA) {
            throw new BusinessRuleException("Solo se pueden cancelar citas reservadas");
        }
        cita.setEstado(Cita.Estado.CANCELADA);
        cita = citaRepository.save(cita);
        notificacionService.cancelar(cita);
        return CitaResponse.desde(cita);
    }

    private void validarReservable(Turno turno) {
        if (!turno.getMedico().isActivo()) {
            throw new BusinessRuleException("El turno pertenece a un médico inactivo");
        }
        if (!turno.getInicio().isAfter(LocalDateTime.now())) {
            throw new BusinessRuleException("No se pueden reservar turnos pasados");
        }
    }
}