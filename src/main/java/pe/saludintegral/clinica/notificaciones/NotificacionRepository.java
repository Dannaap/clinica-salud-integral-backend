package pe.saludintegral.clinica.notificaciones;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    boolean existsByCitaIdAndTipo(Long citaId, Notificacion.Tipo tipo);
    List<Notificacion> findAllByOrderByCreadaEnDesc();
    List<Notificacion> findByCitaPacienteIdOrderByCreadaEnDesc(Long pacienteId);
    List<Notificacion> findByCitaTurnoInicioBetweenAndTipo(LocalDateTime desde, LocalDateTime hasta,
                                                            Notificacion.Tipo tipo);
}