package ni.edu.uam.gestionproductos.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductoResponse(
        Integer id, String codigo, String nombre, String descripcion, BigDecimal precioVenta,
        int existencia, Integer categoriaId, String categoriaNombre,
        Integer proveedorId, String proveedorNombre, List<String> etiquetas
) {
}