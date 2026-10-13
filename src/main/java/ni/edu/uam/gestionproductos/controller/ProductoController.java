package ni.edu.uam.gestionproductos.controller;

import jakarta.validation.Valid;
import ni.edu.uam.gestionproductos.dto.ProductoRequestDTO;
import ni.edu.uam.gestionproductos.dto.ProductoResponse;
import ni.edu.uam.gestionproductos.service.ProductoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {
    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public List<ProductoResponse> listar() {
        return productoService.listar();
    }

    @GetMapping("/categoria/{categoriaId}")
    public List<ProductoResponse> listarPorCategoria(@PathVariable Integer categoriaId) {
        return productoService.listarPorCategoria(categoriaId);
    }

    @GetMapping("/etiqueta/{etiquetaId}")
    public List<ProductoResponse> listarPorEtiqueta(@PathVariable Integer etiquetaId) {
        return productoService.listarPorEtiqueta(etiquetaId);
    }

    @GetMapping("/{id}")
    public ProductoResponse buscar(@PathVariable Integer id) {
        return productoService.buscarPorId(id);
    }

    @PostMapping
    public ProductoResponse guardar(@Valid @RequestBody ProductoRequestDTO request) {
        return productoService.guardar(request);
    }

    @PutMapping("/{id}")
    public ProductoResponse actualizar(@PathVariable Integer id,
                                       @Valid @RequestBody ProductoRequestDTO request) {
        return productoService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{productoId}/etiquetas/{etiquetaId}")
    public ProductoResponse agregarEtiqueta(@PathVariable Integer productoId,
                                            @PathVariable Integer etiquetaId) {
        return productoService.agregarEtiqueta(productoId, etiquetaId);
    }

    @DeleteMapping("/{productoId}/etiquetas/{etiquetaId}")
    public ResponseEntity<Void> eliminarEtiqueta(@PathVariable Integer productoId,
                                                 @PathVariable Integer etiquetaId) {
        productoService.eliminarEtiqueta(productoId, etiquetaId);
        return ResponseEntity.noContent().build();
    }
}