package ni.edu.uam.gestionproductos.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categoria")
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false)
    private boolean activa = true;

    @OneToMany(mappedBy = "categoria")
    private List<Producto> productos = new ArrayList<>();

    protected Categoria() {
    }

    public Categoria(String nombre, boolean activa) {
        this.nombre = nombre;
        this.activa = activa;
    }

    public Integer getId() { return id; }
    public String getNombre() { return nombre; }
    public boolean isActiva() { return activa; }
    public List<Producto> getProductos() { return productos; }
}