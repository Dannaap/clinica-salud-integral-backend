package pe.saludintegral.clinica.especialidades;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.saludintegral.clinica.common.BusinessRuleException;
import pe.saludintegral.clinica.common.ResourceNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EspecialidadService {

    private final EspecialidadRepository repository;

    @Transactional(readOnly = true)
    public List<Especialidad> listar() {
        return repository.findAll();
    }

    public Especialidad crear(Especialidad especialidad) {
        if (repository.existsByNombreIgnoreCase(especialidad.getNombre())) {
            throw new BusinessRuleException("La especialidad ya existe");
        }
        return repository.save(especialidad);
    }

    public Especialidad actualizar(Long id, Especialidad datos) {
        Especialidad actual = buscar(id);
        if (!actual.getNombre().equalsIgnoreCase(datos.getNombre())
                && repository.existsByNombreIgnoreCase(datos.getNombre())) {
            throw new BusinessRuleException("La especialidad ya existe");
        }
        actual.setNombre(datos.getNombre());
        actual.setDescripcion(datos.getDescripcion());
        return repository.save(actual);
    }

    public void eliminar(Long id) {
        repository.delete(buscar(id));
    }

    private Especialidad buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad no encontrada"));
    }
}