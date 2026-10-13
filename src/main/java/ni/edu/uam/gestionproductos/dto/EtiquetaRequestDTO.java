package ni.edu.uam.gestionproductos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EtiquetaRequestDTO(@NotBlank @Size(max = 100) String nombre) {
}