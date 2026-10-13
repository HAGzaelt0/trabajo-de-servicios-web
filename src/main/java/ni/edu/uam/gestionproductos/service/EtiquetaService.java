package ni.edu.uam.gestionproductos.service;

import ni.edu.uam.gestionproductos.dto.EtiquetaRequestDTO;
import ni.edu.uam.gestionproductos.dto.EtiquetaResponse;
import ni.edu.uam.gestionproductos.entity.Etiqueta;
import ni.edu.uam.gestionproductos.repository.EtiquetaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class EtiquetaService {
    private final EtiquetaRepository repository;

    public EtiquetaService(EtiquetaRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<EtiquetaResponse> listar() {
        return repository.findAll().stream().map(EtiquetaService::toResponse).toList();
    }

    @Transactional
    public EtiquetaResponse guardar(EtiquetaRequestDTO request) {
        return repository.findByNombre(request.nombre())
                .map(EtiquetaService::toResponse)
                .orElseGet(() -> toResponse(repository.save(new Etiqueta(request.nombre()))));
    }

    private static EtiquetaResponse toResponse(Etiqueta etiqueta) {
        return new EtiquetaResponse(etiqueta.getId(), etiqueta.getNombre());
    }
}