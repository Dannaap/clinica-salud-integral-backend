package pe.saludintegral.clinica.atenciones;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AtencionRepository extends JpaRepository<Atencion, Long> {
    boolean existsByCitaId(Long citaId);
    List<Atencion> findByCitaPacienteIdOrderByCreadaEnDesc(Long pacienteId);
    List<Atencion> findByMedicoIdOrderByCreadaEnDesc(Long medicoId);
    List<Atencion> findByCreadaEnGreaterThanEqualAndCreadaEnLessThanOrderByCreadaEnDesc(
            LocalDateTime desde, LocalDateTime hasta);
}