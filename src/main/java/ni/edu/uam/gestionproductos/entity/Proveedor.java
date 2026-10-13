package ni.edu.uam.gestionproductos.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "proveedor")
public class Proveedor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(length = 30)
    private String telefono;

    @Column(length = 150, unique = true)
    private String correo;

    @Column(nullable = false)
    private boolean activo = true;

    @OneToMany(mappedBy = "proveedor")
    private List<Producto> productos = new ArrayList<>();

    protected Proveedor() {
    }

    public Proveedor(String nombre, String telefono, String correo, boolean activo) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
        this.activo = activo;
    }

    public Integer getId() { return id; }
    public String getNombre() { return nombre; }
    public String getTelefono() { return telefono; }
    public String getCorreo() { return correo; }
    public boolean isActivo() { return activo; }
    public List<Producto> getProductos() { return productos; }
}