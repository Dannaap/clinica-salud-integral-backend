package pe.saludintegral.clinica;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import pe.saludintegral.clinica.usuarios.Usuario;
import pe.saludintegral.clinica.usuarios.UsuarioRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:bootstrap;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.sql.init.mode=never",
        "app.bootstrap-admin.enabled=true",
        "app.bootstrap-admin.dni=00000000",
        "app.bootstrap-admin.nombres=Admin",
        "app.bootstrap-admin.apellidos=Prueba",
        "app.bootstrap-admin.email=admin@example.test",
        "app.bootstrap-admin.password=FakeAdminPassword123!"
})
@AutoConfigureMockMvc
class BootstrapAdminIntegrationTests {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void creaAdministradorConContrasenaCifrada() {
        Usuario admin = usuarioRepository.findByEmail("admin@example.test").orElseThrow();

        assertEquals(Usuario.Rol.ADMIN, admin.getRol());
        assertTrue(passwordEncoder.matches("FakeAdminPassword123!", admin.getPassword()));
        assertFalse(admin.getPassword().equals("FakeAdminPassword123!"));
    }

    @Test
    void loginValidoDevuelveTokenJwt() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"admin@example.test\",\"password\":\"FakeAdminPassword123!\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.tipo").value("Bearer"));
    }
}