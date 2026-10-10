package pe.saludintegral.clinica;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import pe.saludintegral.clinica.atenciones.AtencionRepository;
import pe.saludintegral.clinica.atenciones.AtencionRequest;
import pe.saludintegral.clinica.atenciones.AtencionService;
import pe.saludintegral.clinica.atenciones.RecetaRepository;
import pe.saludintegral.clinica.atenciones.RecetaRequest;
import pe.saludintegral.clinica.atenciones.RecetaService;
import pe.saludintegral.clinica.citas.CitaRepository;
import pe.saludintegral.clinica.citas.CitaRequest;
import pe.saludintegral.clinica.citas.CitaService;
import pe.saludintegral.clinica.common.BusinessRuleException;
import pe.saludintegral.clinica.especialidades.Especialidad;
import pe.saludintegral.clinica.especialidades.EspecialidadRepository;
import pe.saludintegral.clinica.medicos.Medico;
import pe.saludintegral.clinica.medicos.MedicoRepository;
import pe.saludintegral.clinica.notificaciones.Notificacion;
import pe.saludintegral.clinica.notificaciones.NotificacionRepository;
import pe.saludintegral.clinica.notificaciones.NotificacionService;
import pe.saludintegral.clinica.pacientes.Paciente;
import pe.saludintegral.clinica.pacientes.PacienteRepository;
import pe.saludintegral.clinica.reportes.ReporteService;
import pe.saludintegral.clinica.turnos.Turno;
import pe.saludintegral.clinica.turnos.TurnoRepository;
import pe.saludintegral.clinica.turnos.TurnoRequest;
import pe.saludintegral.clinica.turnos.TurnoService;
import pe.saludintegral.clinica.usuarios.CambiarContrasenaRequest;
import pe.saludintegral.clinica.usuarios.CuentaService;
import pe.saludintegral.clinica.usuarios.Usuario;
import pe.saludintegral.clinica.usuarios.UsuarioRepository;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:workflow;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.sql.init.mode=never",
        "app.notifications.reminder-scan-ms=3600000",
        "app.notifications.no-show-scan-ms=3600000"
})
class WorkflowAcceptanceTests {
    @Autowired private CitaService citaService;
        @Autowired private CitaRepository citaRepository;
    @Autowired private TurnoService turnoService;
    @Autowired private ReporteService reporteService;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PacienteRepository pacienteRepository;
    @Autowired private EspecialidadRepository especialidadRepository;
    @Autowired private MedicoRepository medicoRepository;
    @Autowired private TurnoRepository turnoRepository;
    @Autowired private NotificacionRepository notificacionRepository;
            @Autowired private AtencionService atencionService;
            @Autowired private RecetaService recetaService;
            @Autowired private NotificacionService notificacionService;
            @Autowired private CuentaService cuentaService;
            @Autowired private AtencionRepository atencionRepository;
            @Autowired private RecetaRepository recetaRepository;
            @Autowired private PasswordEncoder passwordEncoder;

    private Long medicoId;
    private Long pacienteId;
    private LocalDateTime inicio;

    @BeforeEach
    void prepararDatosFicticios() {
        notificacionRepository.deleteAll();
        recetaRepository.deleteAll();
        atencionRepository.deleteAll();
        citaRepository.deleteAll();
        turnoRepository.deleteAll();
        medicoRepository.deleteAll();
        especialidadRepository.deleteAll();
        pacienteRepository.deleteAll();
        usuarioRepository.deleteAll();

        Usuario usuario = new Usuario();
        usuario.setDni("87654321");
        usuario.setNombres("Medico");
        usuario.setApellidos("Prueba");
        usuario.setEmail("medico.workflow@example.test");
        usuario.setPassword(passwordEncoder.encode("InitialFakePassword123!"));
        usuario.setRol(Usuario.Rol.MEDICO);
        usuario = usuarioRepository.save(usuario);

        Especialidad especialidad = especialidadRepository.save(new Especialidad(null, "Medicina de prueba", ""));
        Medico medico = new Medico(null, usuario, especialidad, "CMP-00001", true);
        medicoId = medicoRepository.save(medico).getId();

        Paciente paciente = new Paciente();
        paciente.setDni("11223344");
        paciente.setNombres("Paciente");
        paciente.setApellidos("Ficticio");
        paciente.setEmail("paciente@example.test");
        pacienteId = pacienteRepository.save(paciente).getId();
        inicio = LocalDateTime.now().plusDays(2).withSecond(0).withNano(0);
    }

    @Test
    void turnoNoSeSolapaYReservaNotificaUnaSolaCita() {
        var turno = turnoService.crear(new TurnoRequest(medicoId, inicio, inicio.plusMinutes(30)));
        assertThrows(BusinessRuleException.class,
                () -> turnoService.crear(new TurnoRequest(medicoId, inicio.plusMinutes(15), inicio.plusMinutes(45))));

        var cita = citaService.reservar(new CitaRequest(pacienteId, turno.id(), "Control ficticio"));
        assertEquals("RESERVADA", cita.estado().name());
        assertEquals(1, notificacionRepository.count());
        assertEquals(Notificacion.Tipo.CONFIRMACION, notificacionRepository.findAll().get(0).getTipo());
        assertThrows(BusinessRuleException.class,
                () -> citaService.reservar(new CitaRequest(pacienteId, turno.id(), "Duplicada")));
    }

    @Test
    void reprogramarYCancelarCreanNotificacionesDeEstado() {
        var turnoInicial = turnoService.crear(new TurnoRequest(medicoId, inicio, inicio.plusMinutes(30)));
        var turnoNuevo = turnoService.crear(new TurnoRequest(medicoId, inicio.plusHours(1), inicio.plusHours(1).plusMinutes(30)));
        var cita = citaService.reservar(new CitaRequest(pacienteId, turnoInicial.id(), "Control"));

        citaService.reprogramar(cita.id(), turnoNuevo.id());
        citaService.cancelar(cita.id());

        assertEquals(3, notificacionRepository.count());
        assertTrue(notificacionRepository.findAll().stream()
                .anyMatch(notificacion -> notificacion.getTipo() == Notificacion.Tipo.REPROGRAMACION));
        assertTrue(notificacionRepository.findAll().stream()
                .anyMatch(notificacion -> notificacion.getTipo() == Notificacion.Tipo.CANCELACION));
    }

    @Test
    void reportesSeGeneranEnPdfYExcel() throws Exception {
        var pdf = reporteService.pdf(LocalDate.now());
        var excel = reporteService.excel(LocalDate.now());

        assertEquals("application/pdf", pdf.contentType());
        assertTrue(new String(pdf.contenido(), 0, 4).startsWith("%PDF"));
        assertTrue(excel.contentType().contains("spreadsheetml"));
        assertArrayEquals(new byte[]{'P', 'K'}, new byte[]{excel.contenido()[0], excel.contenido()[1]});
    }

            @Test
            void registraHistoriaConCie10YRecetaDigital() {
            var turno = turnoService.crear(new TurnoRequest(medicoId, inicio, inicio.plusMinutes(30)));
            var cita = citaService.reservar(new CitaRequest(pacienteId, turno.id(), "Consulta"));
            Usuario medico = usuarioRepository.findByEmail("medico.workflow@example.test").orElseThrow();
            var authentication = new UsernamePasswordAuthenticationToken(medico, null, medico.getAuthorities());

            var atencion = atencionService.registrar(new AtencionRequest(cita.id(), "Fiebre", "Cuadro viral",
                "J06.9", "Reposo e hidratación"), authentication);
            var receta = recetaService.crear(atencion.id(),
                new RecetaRequest("Paracetamol", "500 mg", "Cada 8 horas", "3 días"), authentication);

            assertEquals("J06.9", atencion.codigoCie10());
            assertEquals("Paracetamol", receta.medicamento());
            assertEquals("ATENDIDA", citaRepository.findById(cita.id()).orElseThrow().getEstado().name());
            }

            @Test
            void generaRecordatorioYMarcaCitasVencidasComoAusentes() {
            var medico = medicoRepository.findById(medicoId).orElseThrow();
            LocalDateTime ahora = LocalDateTime.now();
            Turno turnoRecordatorio = turnoRepository.save(new pe.saludintegral.clinica.turnos.Turno(null, medico,
                ahora.plusHours(23).plusMinutes(30), ahora.plusHours(24), null));
            var citaRecordatorio = new pe.saludintegral.clinica.citas.Cita(null,
                pacienteRepository.findById(pacienteId).orElseThrow(), turnoRecordatorio,
                pe.saludintegral.clinica.citas.Cita.Estado.RESERVADA, "Recordatorio", ahora);
            citaRepository.save(citaRecordatorio);

            Turno turnoVencido = turnoRepository.save(new pe.saludintegral.clinica.turnos.Turno(null, medico,
                ahora.minusHours(2), ahora.minusHours(1), null));
            var citaVencida = citaRepository.save(new pe.saludintegral.clinica.citas.Cita(null,
                pacienteRepository.findById(pacienteId).orElseThrow(), turnoVencido,
                pe.saludintegral.clinica.citas.Cita.Estado.RESERVADA, "Ausencia", ahora.minusDays(1)));

            notificacionService.generarRecordatorios();
            notificacionService.marcarCitasAusentes();

            assertTrue(notificacionRepository.findAll().stream()
                .anyMatch(notificacion -> notificacion.getTipo() == Notificacion.Tipo.RECORDATORIO));
            assertEquals("AUSENTE", citaRepository.findById(citaVencida.getId()).orElseThrow().getEstado().name());
            }

            @Test
            void usuarioPuedeCambiarSuContrasenaVerificandoLaActual() {
            Usuario usuario = usuarioRepository.findByEmail("medico.workflow@example.test").orElseThrow();
            var authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());

            cuentaService.cambiarContrasena(new CambiarContrasenaRequest(
                "InitialFakePassword123!", "NewPass8"), authentication);

            Usuario actualizado = usuarioRepository.findById(usuario.getId()).orElseThrow();
            assertTrue(passwordEncoder.matches("NewPass8", actualizado.getPassword()));
            }
}