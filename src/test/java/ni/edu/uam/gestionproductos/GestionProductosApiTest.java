package ni.edu.uam.gestionproductos;

import com.fasterxml.jackson.databind.ObjectMapper;
import ni.edu.uam.gestionproductos.repository.CategoriaRepository;
import ni.edu.uam.gestionproductos.repository.EtiquetaRepository;
import ni.edu.uam.gestionproductos.repository.ProductoRepository;
import ni.edu.uam.gestionproductos.repository.ProveedorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GestionProductosApiTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private ProveedorRepository proveedorRepository;
    @Autowired
    private CategoriaRepository categoriaRepository;
        @Autowired
        private EtiquetaRepository etiquetaRepository;

    @BeforeEach
    void limpiarDatos() {
        productoRepository.deleteAll();
        etiquetaRepository.deleteAll();
        proveedorRepository.deleteAll();
        categoriaRepository.deleteAll();
    }

    @Test
    void creaYConsultaProductoConCategoriaYProveedor() throws Exception {
        MvcResult categoria = mockMvc.perform(post("/api/categorias")
                        .contentType("application/json")
                        .content("""
                                {"nombre":"Computadoras","activa":true}
                                """))
                .andExpect(status().isOk())
                .andReturn();
        int categoriaId = objectMapper.readTree(categoria.getResponse().getContentAsString())
                .get("id").asInt();

        MvcResult proveedor = mockMvc.perform(post("/api/proveedores")
                        .contentType("application/json")
                        .content("""
                                {"nombre":"Distribuidora Central","telefono":"2255-0101","correo":"central@example.com","activo":true}
                                """))
                .andExpect(status().isOk())
                .andReturn();
        int proveedorId = objectMapper.readTree(proveedor.getResponse().getContentAsString())
                .get("id").asInt();

        String solicitud = """
                {"codigo":"LAP-001","nombre":"Laptop Lenovo","descripcion":"Equipo de prueba",
                 "precioVenta":850.00,"existencia":10,"categoriaId":%d,"proveedorId":%d}
                """.formatted(categoriaId, proveedorId);

        mockMvc.perform(post("/api/productos")
                        .contentType("application/json")
                        .content(solicitud))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoriaNombre").value("Computadoras"))
                .andExpect(jsonPath("$.proveedorNombre").value("Distribuidora Central"));

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].codigo").value("LAP-001"));
    }

    @Test
    void completaCrudYRelacionesDeEtiquetas() throws Exception {
        var categoria = categoriaRepository.save(new ni.edu.uam.gestionproductos.entity.Categoria(
                "Computadoras", true));
        var proveedor = proveedorRepository.save(new ni.edu.uam.gestionproductos.entity.Proveedor(
                "Distribuidora Central", "2255-0101", "central-test@example.com", true));
        String laptopRequest = """
                {"codigo":"LAP-002","nombre":"Laptop","descripcion":"Equipo de prueba",
                 "precioVenta":800.00,"existencia":5,"categoriaId":%d,"proveedorId":%d}
                """.formatted(categoria.getId(), proveedor.getId());
        String mouseRequest = """
                {"codigo":"ACC-002","nombre":"Mouse","descripcion":"Equipo de prueba",
                 "precioVenta":20.00,"existencia":12,"categoriaId":%d,"proveedorId":%d}
                """.formatted(categoria.getId(), proveedor.getId());

        MvcResult laptop = mockMvc.perform(post("/api/productos")
                        .contentType("application/json").content(laptopRequest))
                .andExpect(status().isOk()).andReturn();
        int laptopId = objectMapper.readTree(laptop.getResponse().getContentAsString()).get("id").asInt();
        MvcResult mouse = mockMvc.perform(post("/api/productos")
                        .contentType("application/json").content(mouseRequest))
                .andExpect(status().isOk()).andReturn();
        int mouseId = objectMapper.readTree(mouse.getResponse().getContentAsString()).get("id").asInt();
        MvcResult etiqueta = mockMvc.perform(post("/api/etiquetas")
                        .contentType("application/json").content("{\"nombre\":\"Gaming\"}"))
                .andExpect(status().isOk()).andReturn();
        int etiquetaId = objectMapper.readTree(etiqueta.getResponse().getContentAsString()).get("id").asInt();

        mockMvc.perform(post("/api/productos/{productoId}/etiquetas/{etiquetaId}", laptopId, etiquetaId))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/productos/{productoId}/etiquetas/{etiquetaId}", mouseId, etiquetaId))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/productos/categoria/{id}", categoria.getId()))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(2)));
        mockMvc.perform(get("/api/productos/etiqueta/{id}", etiquetaId))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(2)));

        mockMvc.perform(put("/api/productos/{id}", laptopId)
                        .contentType("application/json")
                        .content("""
                                {"codigo":"LAP-002","nombre":"Laptop actualizada","descripcion":"Equipo actualizado",
                                 "precioVenta":900.00,"existencia":8,"categoriaId":%d,"proveedorId":%d}
                                """.formatted(categoria.getId(), proveedor.getId())))
                .andExpect(status().isOk()).andExpect(jsonPath("$.nombre").value("Laptop actualizada"));

        mockMvc.perform(delete("/api/productos/{productoId}/etiquetas/{etiquetaId}", laptopId, etiquetaId))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/productos/{id}", laptopId))
                .andExpect(status().isOk()).andExpect(jsonPath("$.etiquetas", hasSize(0)));
        mockMvc.perform(get("/api/etiquetas"))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(delete("/api/productos/{id}", mouseId)).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/productos/{id}", mouseId)).andExpect(status().isNotFound());
        mockMvc.perform(get("/api/productos/etiqueta/{id}", etiquetaId))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
    }
}