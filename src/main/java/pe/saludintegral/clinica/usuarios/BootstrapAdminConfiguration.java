package pe.saludintegral.clinica.usuarios;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@ConditionalOnProperty(prefix = "app.bootstrap-admin", name = "enabled", havingValue = "true")
public class BootstrapAdminConfiguration {

    @Bean
    CommandLineRunner bootstrapAdmin(UsuarioRepository usuarioRepository,
                                     PasswordEncoder passwordEncoder,
                                     Environment environment) {
        return args -> {
            if (usuarioRepository.count() > 0) {
                return;
            }

            String password = environment.getRequiredProperty("app.bootstrap-admin.password");
            if (password.length() < 8) {
                throw new IllegalArgumentException("La contraseña inicial debe tener al menos 8 caracteres");
            }

            Usuario admin = new Usuario();
            admin.setDni(requiredProperty(environment, "app.bootstrap-admin.dni"));
            admin.setNombres(requiredProperty(environment, "app.bootstrap-admin.nombres"));
            admin.setApellidos(requiredProperty(environment, "app.bootstrap-admin.apellidos"));
            admin.setEmail(requiredProperty(environment, "app.bootstrap-admin.email"));
            admin.setPassword(passwordEncoder.encode(password));
            admin.setRol(Usuario.Rol.ADMIN);
            usuarioRepository.save(admin);
        };
    }

    private String requiredProperty(Environment environment, String name) {
        String value = environment.getRequiredProperty(name).trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException(name + " no puede estar vacío");
        }
        return value;
    }
}