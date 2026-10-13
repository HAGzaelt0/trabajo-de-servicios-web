package ni.edu.uam.gestionproductos.service;

import ni.edu.uam.gestionproductos.dto.ProductoRequestDTO;
import ni.edu.uam.gestionproductos.dto.ProductoResponse;
import ni.edu.uam.gestionproductos.entity.Categoria;
import ni.edu.uam.gestionproductos.entity.Etiqueta;
import ni.edu.uam.gestionproductos.entity.Producto;
import ni.edu.uam.gestionproductos.entity.Proveedor;
import ni.edu.uam.gestionproductos.repository.CategoriaRepository;
import ni.edu.uam.gestionproductos.repository.EtiquetaRepository;
import ni.edu.uam.gestionproductos.repository.ProductoRepository;
import ni.edu.uam.gestionproductos.repository.ProveedorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class ProductoService {
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProveedorRepository proveedorRepository;
    private final EtiquetaRepository etiquetaRepository;

    public ProductoService(ProductoRepository productoRepository,
                           CategoriaRepository categoriaRepository,
                           ProveedorRepository proveedorRepository,
                           EtiquetaRepository etiquetaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.proveedorRepository = proveedorRepository;
        this.etiquetaRepository = etiquetaRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> listar() {
        return productoRepository.findAll().stream().map(ProductoService::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProductoResponse buscarPorId(Integer id) {
        return toResponse(buscarProducto(id));
    }

    @Transactional
    public ProductoResponse guardar(ProductoRequestDTO dto) {
        Producto producto = new Producto(dto.getCodigo(), dto.getNombre(), dto.getDescripcion(),
                dto.getPrecioVenta(), dto.getExistencia(), buscarCategoria(dto.getCategoriaId()),
                buscarProveedor(dto.getProveedorId()));
        return toResponse(productoRepository.save(producto));
    }

    @Transactional
    public ProductoResponse actualizar(Integer id, ProductoRequestDTO dto) {
        Producto producto = buscarProducto(id);
        producto.setCodigo(dto.getCodigo());
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecioVenta(dto.getPrecioVenta());
        producto.setExistencia(dto.getExistencia());
        producto.setCategoria(buscarCategoria(dto.getCategoriaId()));
        producto.setProveedor(buscarProveedor(dto.getProveedorId()));
        return toResponse(productoRepository.save(producto));
    }

    @Transactional
    public void eliminar(Integer id) {
        productoRepository.delete(buscarProducto(id));
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> listarPorCategoria(Integer categoriaId) {
        if (!categoriaRepository.existsById(categoriaId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria no encontrada");
        }
        return productoRepository.findByCategoriaId(categoriaId).stream()
                .map(ProductoService::toResponse).toList();
    }

    @Transactional
    public ProductoResponse agregarEtiqueta(Integer productoId, Integer etiquetaId) {
        Producto producto = buscarProducto(productoId);
        Etiqueta etiqueta = buscarEtiqueta(etiquetaId);
        producto.agregarEtiqueta(etiqueta);
        return toResponse(productoRepository.save(producto));
    }

    @Transactional
    public void eliminarEtiqueta(Integer productoId, Integer etiquetaId) {
        Producto producto = buscarProducto(productoId);
        Etiqueta etiqueta = buscarEtiqueta(etiquetaId);
        if (!producto.eliminarEtiqueta(etiqueta)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La etiqueta no esta asociada al producto");
        }
        productoRepository.save(producto);
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> listarPorEtiqueta(Integer etiquetaId) {
        buscarEtiqueta(etiquetaId);
        return productoRepository.findDistinctByEtiquetas_Id(etiquetaId).stream()
                .map(ProductoService::toResponse).toList();
    }

    private Producto buscarProducto(Integer id) {
        return productoRepository.findDetailedById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
    }

    private Categoria buscarCategoria(Integer id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria no encontrada"));
    }

    private Proveedor buscarProveedor(Integer id) {
        return proveedorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proveedor no encontrado"));
    }

    private Etiqueta buscarEtiqueta(Integer id) {
        return etiquetaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Etiqueta no encontrada"));
    }

    private static ProductoResponse toResponse(Producto producto) {
        Categoria categoria = producto.getCategoria();
        Proveedor proveedor = producto.getProveedor();
        List<String> etiquetas = producto.getEtiquetas().stream().map(Etiqueta::getNombre).sorted().toList();
        return new ProductoResponse(producto.getId(), producto.getCodigo(), producto.getNombre(),
                producto.getDescripcion(), producto.getPrecioVenta(), producto.getExistencia(),
                categoria.getId(), categoria.getNombre(), proveedor == null ? null : proveedor.getId(),
            proveedor == null ? null : proveedor.getNombre(), etiquetas);
    }
}