package pe.saludintegral.clinica.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;
import pe.saludintegral.clinica.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/usuarios/**").hasRole("ADMIN")
                .requestMatchers("/api/pacientes/**").hasAnyRole("ADMIN", "RECEPCION")
                .requestMatchers("/api/cuenta/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/citas/**").hasAnyRole("ADMIN", "RECEPCION", "MEDICO")
                .requestMatchers("/api/citas/**").hasAnyRole("ADMIN", "RECEPCION")
                .requestMatchers("/api/atenciones/**").hasAnyRole("ADMIN", "MEDICO")
                .requestMatchers("/api/reportes/**").hasRole("ADMIN")
                .requestMatchers("/api/notificaciones/**").hasAnyRole("ADMIN", "RECEPCION")
                .requestMatchers(HttpMethod.GET, "/api/especialidades/**", "/api/medicos/**", "/api/turnos/**")
                    .hasAnyRole("ADMIN", "RECEPCION", "MEDICO")
                .requestMatchers("/api/especialidades/**", "/api/medicos/**", "/api/turnos/**")
                    .hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}