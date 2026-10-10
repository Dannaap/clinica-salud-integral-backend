package pe.saludintegral.clinica.medicos;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.saludintegral.clinica.common.BusinessRuleException;
import pe.saludintegral.clinica.common.ResourceNotFoundException;
import pe.saludintegral.clinica.especialidades.EspecialidadRepository;
import pe.saludintegral.clinica.usuarios.Usuario;
import pe.saludintegral.clinica.usuarios.UsuarioRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MedicoService {

    private final MedicoRepository medicoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EspecialidadRepository especialidadRepository;

    @Transactional(readOnly = true)
    public List<MedicoResponse> listar() {
        return medicoRepository.findAll().stream().map(MedicoResponse::desde).toList();
    }

    public MedicoResponse crear(MedicoRequest request) {
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        if (usuario.getRol() != Usuario.Rol.MEDICO) {
            throw new BusinessRuleException("El usuario debe tener el rol MEDICO");
        }
        if (medicoRepository.existsByUsuarioId(usuario.getId())) {
            throw new BusinessRuleException("El usuario ya tiene un perfil médico");
        }
        if (medicoRepository.existsByColegiatura(request.colegiatura())) {
            throw new BusinessRuleException("La colegiatura ya está registrada");
        }
        var especialidad = especialidadRepository.findById(request.especialidadId())
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad no encontrada"));
        Medico medico = new Medico(null, usuario, especialidad, request.colegiatura().trim(), true);
        return MedicoResponse.desde(medicoRepository.save(medico));
    }

    public MedicoResponse actualizar(Long id, MedicoRequest request) {
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médico no encontrado"));
        if (!medico.getUsuario().getId().equals(request.usuarioId())) {
            throw new BusinessRuleException("No se puede cambiar el usuario asociado al perfil médico");
        }
        if (!medico.getColegiatura().equals(request.colegiatura())
                && medicoRepository.existsByColegiatura(request.colegiatura())) {
            throw new BusinessRuleException("La colegiatura ya está registrada");
        }
        medico.setEspecialidad(especialidadRepository.findById(request.especialidadId())
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad no encontrada")));
        medico.setColegiatura(request.colegiatura().trim());
        return MedicoResponse.desde(medicoRepository.save(medico));
    }

    public void eliminar(Long id) {
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médico no encontrado"));
        medico.setActivo(false);
        medicoRepository.save(medico);
    }
}