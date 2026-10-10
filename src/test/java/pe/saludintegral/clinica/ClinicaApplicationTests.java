package pe.saludintegral.clinica;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import pe.saludintegral.clinica.usuarios.Usuario;
import pe.saludintegral.clinica.usuarios.UsuarioRepository;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:clinica;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.jpa.hibernate.ddl-auto=create-drop",
		"spring.sql.init.mode=never"
})
@AutoConfigureMockMvc
class ClinicaApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@BeforeEach
	void limpiarUsuarios() {
		usuarioRepository.deleteAll();
	}

	@Test
	void contextLoads() {
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void listarUsuariosNoExponeContrasenas() throws Exception {
		Usuario usuario = new Usuario();
		usuario.setDni("12345678");
		usuario.setNombres("Ana");
		usuario.setApellidos("Prueba");
		usuario.setEmail("ana@example.test");
		usuario.setPassword("hash-no-exponer");
		usuario.setRol(Usuario.Rol.ADMIN);
		usuarioRepository.save(usuario);

		mockMvc.perform(get("/api/usuarios").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data[0].email").value("ana@example.test"))
				.andExpect(jsonPath("$.data[0].password").doesNotExist());
	}

	@Test
	void loginConCredencialesIncorrectasRespondeNoAutorizado() throws Exception {
		mockMvc.perform(post("/api/auth/login")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"email\":\"no-existe@example.test\",\"password\":\"incorrecta\"}"))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.message").value("Credenciales inválidas"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void crearUsuarioRechazaContrasenaMenorDeOchoCaracteres() throws Exception {
		mockMvc.perform(post("/api/usuarios")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"dni\":\"12340000\",\"nombres\":\"Demo\",\"apellidos\":\"Usuario\","
							+ "\"email\":\"demo@example.test\",\"password\":\"1234567\",\"rol\":\"RECEPCION\"}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void crearUsuarioAceptaContrasenaDeOchoCaracteres() throws Exception {
		mockMvc.perform(post("/api/usuarios")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"dni\":\"12340001\",\"nombres\":\"Demo\",\"apellidos\":\"Usuario\","
							+ "\"email\":\"demo8@example.test\",\"password\":\"Abc12345\",\"rol\":\"RECEPCION\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.email").value("demo8@example.test"))
				.andExpect(jsonPath("$.data.password").doesNotExist());
	}
}
