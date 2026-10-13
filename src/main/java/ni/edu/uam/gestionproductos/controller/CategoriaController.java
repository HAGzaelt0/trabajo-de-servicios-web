package ni.edu.uam.gestionproductos.controller;

import jakarta.validation.Valid;
import ni.edu.uam.gestionproductos.dto.CategoriaRequest;
import ni.edu.uam.gestionproductos.dto.CategoriaResponse;
import ni.edu.uam.gestionproductos.entity.Categoria;
import ni.edu.uam.gestionproductos.repository.CategoriaRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {
    private final CategoriaRepository repository;

    public CategoriaController(CategoriaRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<CategoriaResponse> listar() {
        return repository.findAll().stream().map(CategoriaController::toResponse).toList();
    }

    @PostMapping
    public CategoriaResponse guardar(@Valid @RequestBody CategoriaRequest request) {
        boolean activa = request.activa() == null || request.activa();
        return toResponse(repository.save(new Categoria(request.nombre(), activa)));
    }

    private static CategoriaResponse toResponse(Categoria categoria) {
        return new CategoriaResponse(categoria.getId(), categoria.getNombre(), categoria.isActiva());
    }
}