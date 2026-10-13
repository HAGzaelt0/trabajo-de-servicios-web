package ni.edu.uam.gestionproductos.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ProductoRequest(
        @NotBlank @Size(max = 30) String codigo,
        @NotBlank @Size(max = 150) String nombre,
        @Size(max = 500) String descripcion,
        @NotNull @DecimalMin("0.01") BigDecimal precioVenta,
        @NotNull @PositiveOrZero Integer existencia,
        @NotNull @Positive Integer categoriaId,
        @NotNull @Positive Integer proveedorId
) {
}