package ni.edu.uam.gestionproductos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProveedorRequest(
        @NotBlank @Size(max = 120) String nombre,
        @Size(max = 30) String telefono,
        @Email @Size(max = 150) String correo,
        Boolean activo
) {
}