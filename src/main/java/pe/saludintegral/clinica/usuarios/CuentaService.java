package pe.saludintegral.clinica.usuarios;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import pe.saludintegral.clinica.common.BusinessRuleException;
import pe.saludintegral.clinica.common.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class CuentaService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UsuarioDTO obtener(Authentication authentication) {
        return UsuarioDTO.desde(obtenerUsuario(authentication));
    }

    public UsuarioDTO actualizar(ActualizarCuentaRequest request, Authentication authentication) {
        Usuario usuario = obtenerUsuario(authentication);
        if (!usuario.getEmail().equalsIgnoreCase(request.email().trim())
                && usuarioRepository.existsByEmailAndIdNot(request.email().trim(), usuario.getId())) {
            throw new BusinessRuleException("El email ya está en uso");
        }
        usuario.setNombres(request.nombres().trim());
        usuario.setApellidos(request.apellidos().trim());
        usuario.setEmail(request.email().trim());
        return UsuarioDTO.desde(usuarioRepository.save(usuario));
    }

    public void cambiarContrasena(CambiarContrasenaRequest request, Authentication authentication) {
        Usuario usuario = obtenerUsuario(authentication);
        if (!passwordEncoder.matches(request.contrasenaActual(), usuario.getPassword())) {
            throw new BusinessRuleException("La contraseña actual no es correcta");
        }
        usuario.setPassword(passwordEncoder.encode(request.contrasenaNueva()));
        usuarioRepository.save(usuario);
    }

    private Usuario obtenerUsuario(Authentication authentication) {
        return usuarioRepository.findById(((Usuario) authentication.getPrincipal()).getId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }
}