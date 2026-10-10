package pe.saludintegral.clinica.citas;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CitaRepository extends JpaRepository<Cita, Long> {
    boolean existsByTurnoIdAndEstado(Long turnoId, Cita.Estado estado);
    boolean existsByTurnoId(Long turnoId);
    List<Cita> findByTurnoMedicoUsuarioIdOrderByTurnoInicio(Long usuarioId);
    List<Cita> findByEstadoAndTurno_InicioBetween(Cita.Estado estado, LocalDateTime desde, LocalDateTime hasta);
    @Query("select count(cita) from Cita cita where cita.estado = :estado and cita.turno.inicio >= :desde and cita.turno.inicio < :hasta")
    long contarPorEstadoEnRango(@Param("estado") Cita.Estado estado,
                                @Param("desde") LocalDateTime desde,
                                @Param("hasta") LocalDateTime hasta);
    List<Cita> findByEstadoAndTurno_FinBefore(Cita.Estado estado, LocalDateTime antesDe);
}