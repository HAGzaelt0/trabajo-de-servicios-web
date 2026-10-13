package ni.edu.uam.gestionproductos.controller;

import jakarta.validation.Valid;
import ni.edu.uam.gestionproductos.dto.ProveedorRequest;
import ni.edu.uam.gestionproductos.dto.ProveedorResponse;
import ni.edu.uam.gestionproductos.entity.Proveedor;
import ni.edu.uam.gestionproductos.repository.ProveedorRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
public class ProveedorController {
    private final ProveedorRepository repository;

    public ProveedorController(ProveedorRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<ProveedorResponse> listar() {
        return repository.findAll().stream().map(ProveedorController::toResponse).toList();
    }

    @PostMapping
    public ProveedorResponse guardar(@Valid @RequestBody ProveedorRequest request) {
        boolean activo = request.activo() == null || request.activo();
        Proveedor proveedor = new Proveedor(request.nombre(), request.telefono(), request.correo(), activo);
        return toResponse(repository.save(proveedor));
    }

    private static ProveedorResponse toResponse(Proveedor proveedor) {
        return new ProveedorResponse(proveedor.getId(), proveedor.getNombre(), proveedor.getTelefono(),
                proveedor.getCorreo(), proveedor.isActivo());
    }
}