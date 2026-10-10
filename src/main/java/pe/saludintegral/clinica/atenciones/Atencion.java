package pe.saludintegral.clinica.atenciones;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.saludintegral.clinica.citas.Cita;
import pe.saludintegral.clinica.usuarios.Usuario;

@Entity
@Table(name = "atenciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Atencion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "cita_id", nullable = false, unique = true)
    private Cita cita;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "medico_usuario_id", nullable = false)
    private Usuario medico;

    @Column(nullable = false, length = 2000)
    private String motivoConsulta;

    @Column(nullable = false, length = 4000)
    private String diagnostico;

    @Column(nullable = false, length = 10)
    private String codigoCie10;

    @Column(length = 6000)
    private String notas;

    @Column(nullable = false)
    private LocalDateTime creadaEn = LocalDateTime.now();
}