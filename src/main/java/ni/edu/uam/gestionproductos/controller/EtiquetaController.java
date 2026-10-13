package ni.edu.uam.gestionproductos.controller;

import jakarta.validation.Valid;
import ni.edu.uam.gestionproductos.dto.EtiquetaRequestDTO;
import ni.edu.uam.gestionproductos.dto.EtiquetaResponse;
import ni.edu.uam.gestionproductos.service.EtiquetaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/etiquetas")
public class EtiquetaController {
    private final EtiquetaService etiquetaService;

    public EtiquetaController(EtiquetaService etiquetaService) {
        this.etiquetaService = etiquetaService;
    }

    @GetMapping
    public List<EtiquetaResponse> listar() {
        return etiquetaService.listar();
    }

    @PostMapping
    public EtiquetaResponse guardar(@Valid @RequestBody EtiquetaRequestDTO request) {
        return etiquetaService.guardar(request);
    }
}