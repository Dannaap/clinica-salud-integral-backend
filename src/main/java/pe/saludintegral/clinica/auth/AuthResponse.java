package pe.saludintegral.clinica.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String tipo;
    private String email;
    private String rol;

    public AuthResponse(String token, String email, String rol) {
        this.token = token;
        this.tipo = "Bearer";
        this.email = email;
        this.rol = rol;
    }
}