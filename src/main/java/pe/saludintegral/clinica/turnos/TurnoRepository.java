package pe.saludintegral.clinica.turnos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TurnoRepository extends JpaRepository<Turno, Long> {
    boolean existsByMedicoIdAndInicioLessThanAndFinGreaterThan(Long medicoId, LocalDateTime fin, LocalDateTime inicio);
    List<Turno> findByMedicoIdAndInicioBetweenOrderByInicio(Long medicoId, LocalDateTime inicio, LocalDateTime fin);
    List<Turno> findByInicioBetweenOrderByInicio(LocalDateTime inicio, LocalDateTime fin);
    List<Turno> findByMedicoIdOrderByInicio(Long medicoId);

    @Query(value = "select * from turnos where id = :id for update", nativeQuery = true)
    Optional<Turno> findByIdForUpdate(@Param("id") Long id);
}