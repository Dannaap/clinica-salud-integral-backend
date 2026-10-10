package pe.saludintegral.clinica.turnos;

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
import pe.saludintegral.clinica.medicos.MedicoRepository;
import pe.saludintegral.clinica.usuarios.Usuario;

@Service
@RequiredArgsConstructor
@Transactional
public class TurnoService {
    private final TurnoRepository turnoRepository;
    private final MedicoRepository medicoRepository;
    private final CitaRepository citaRepository;

    @Transactional(readOnly = true)
    public List<TurnoResponse> listar(Long medicoId, LocalDateTime desde, LocalDateTime hasta,
                                      Authentication authentication) {
        boolean rolMedico = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_MEDICO"));
        if (rolMedico) {
            Long usuarioId = ((Usuario) authentication.getPrincipal()).getId();
            medicoId = medicoRepository.findByUsuarioId(usuarioId)
                    .orElseThrow(() -> new ResourceNotFoundException("El usuario no tiene perfil médico"))
                    .getId();
        }
        if ((desde == null) != (hasta == null)) {
            throw new BusinessRuleException("Debe indicar desde y hasta para filtrar por fecha");
        }
        if (desde != null && hasta != null && !hasta.isAfter(desde)) {
            throw new BusinessRuleException("La fecha hasta debe ser posterior a desde");
        }
        List<Turno> turnos;
        if (desde == null && medicoId == null) {
            turnos = turnoRepository.findAll();
        } else if (desde == null) {
            turnos = turnoRepository.findByMedicoIdOrderByInicio(medicoId);
        } else if (medicoId == null) {
            turnos = turnoRepository.findByInicioBetweenOrderByInicio(desde, hasta);
        } else {
            turnos = turnoRepository.findByMedicoIdAndInicioBetweenOrderByInicio(medicoId, desde, hasta);
        }
        return turnos.stream().map(turno -> TurnoResponse.desde(turno,
                !citaRepository.existsByTurnoIdAndEstado(turno.getId(), Cita.Estado.RESERVADA))).toList();
    }

    public TurnoResponse crear(TurnoRequest request) {
        validarRango(request.inicio(), request.fin());
        var medicoId = Objects.requireNonNull(request.medicoId(), "medicoId es obligatorio");
        var medico = medicoRepository.findById(medicoId)
                .orElseThrow(() -> new ResourceNotFoundException("Médico no encontrado"));
        if (!medico.isActivo()) {
            throw new BusinessRuleException("No se pueden crear turnos para un médico inactivo");
        }
        validarSolapamiento(medico.getId(), request.inicio(), request.fin());
        return TurnoResponse.desde(turnoRepository.save(new Turno(null, medico, request.inicio(), request.fin(), null)), true);
    }

    public TurnoResponse actualizar(Long id, TurnoRequest request) {
        validarRango(request.inicio(), request.fin());
        Turno turno = turnoRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Turno no encontrado"));
        if (!turno.getMedico().getId().equals(request.medicoId())) {
            throw new BusinessRuleException("No se puede cambiar el médico de un turno existente");
        }
        if (citaRepository.existsByTurnoId(turno.getId())) {
            throw new BusinessRuleException("No se puede modificar un turno asociado a una cita");
        }
        if (turnoRepository.existsByMedicoIdAndInicioLessThanAndFinGreaterThan(
                turno.getMedico().getId(), request.fin(), request.inicio())) {
            boolean conflicto = turnoRepository.findAll().stream().anyMatch(otro -> !otro.getId().equals(id)
                    && otro.getMedico().getId().equals(turno.getMedico().getId())
                    && otro.getInicio().isBefore(request.fin()) && otro.getFin().isAfter(request.inicio()));
            if (conflicto) {
                throw new BusinessRuleException("El turno se solapa con otro turno del médico");
            }
        }
        turno.setInicio(request.inicio());
        turno.setFin(request.fin());
        return TurnoResponse.desde(turnoRepository.save(turno), true);
    }

    public void eliminar(Long id) {
        Turno turno = turnoRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Turno no encontrado"));
        if (citaRepository.existsByTurnoId(id)) {
            throw new BusinessRuleException("No se puede eliminar un turno asociado a una cita");
        }
        turnoRepository.deleteById(id);
    }

    private void validarRango(LocalDateTime inicio, LocalDateTime fin) {
        if (inicio == null || fin == null || !inicio.isAfter(LocalDateTime.now()) || !fin.isAfter(inicio)) {
            throw new BusinessRuleException("El turno debe iniciar en el futuro y finalizar después de su inicio");
        }
    }

    private void validarSolapamiento(Long medicoId, LocalDateTime inicio, LocalDateTime fin) {
        if (turnoRepository.existsByMedicoIdAndInicioLessThanAndFinGreaterThan(medicoId, fin, inicio)) {
            throw new BusinessRuleException("El turno se solapa con otro turno del médico");
        }
    }
}