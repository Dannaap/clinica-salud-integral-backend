package pe.saludintegral.clinica.atenciones;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RecetaRepository extends JpaRepository<Receta, Long> {
    List<Receta> findByAtencionIdOrderById(Long atencionId);
}