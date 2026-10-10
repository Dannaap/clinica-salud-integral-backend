package pe.saludintegral.clinica.medicos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicoRepository extends JpaRepository<Medico, Long> {
    Optional<Medico> findByUsuarioId(Long usuarioId);
    boolean existsByUsuarioId(Long usuarioId);
    boolean existsByColegiatura(String colegiatura);
}