package pe.saludintegral.clinica.usuarios;

import lombok.Data;

@Data
public class UsuarioDTO {
    private Long id;
    private String dni;
    private String nombres;
    private String apellidos;
    private String email;
    private Usuario.Rol rol;

    public static UsuarioDTO desde(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setDni(usuario.getDni());
        dto.setNombres(usuario.getNombres());
        dto.setApellidos(usuario.getApellidos());
        dto.setEmail(usuario.getEmail());
        dto.setRol(usuario.getRol());
        return dto;
    }
}