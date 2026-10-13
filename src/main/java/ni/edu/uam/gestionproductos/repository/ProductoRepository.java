package ni.edu.uam.gestionproductos.repository;

import ni.edu.uam.gestionproductos.entity.Producto;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    @Override
    @EntityGraph(attributePaths = {"categoria", "proveedor", "etiquetas"})
    List<Producto> findAll();

    @EntityGraph(attributePaths = {"categoria", "proveedor", "etiquetas"})
    Optional<Producto> findDetailedById(Integer id);

    @EntityGraph(attributePaths = {"categoria", "proveedor", "etiquetas"})
    List<Producto> findByCategoriaId(Integer categoriaId);

    @EntityGraph(attributePaths = {"categoria", "proveedor", "etiquetas"})
    List<Producto> findDistinctByEtiquetas_Id(Integer etiquetaId);
}