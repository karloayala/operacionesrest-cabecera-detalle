package peru.edu.uls.ucos.operacionesrest.direccion;

import org.springframework.stereotype.Service;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoNoEncontradoException;

@Service
public class DireccionService {

    private final DireccionRepository repository;
    private final DireccionMapper mapper;

    public DireccionService(DireccionRepository repository, DireccionMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public DireccionResponse consultarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::aRespuesta)
                .orElseThrow(() -> new RecursoNoEncontradoException("Dirección no encontrada con el ID: " + id));
    }
}