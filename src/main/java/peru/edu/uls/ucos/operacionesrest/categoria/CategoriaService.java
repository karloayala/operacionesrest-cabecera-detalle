package peru.edu.uls.ucos.operacionesrest.categoria;

import org.springframework.stereotype.Service;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoNoEncontradoException;

@Service
public class CategoriaService {
    
    private final CategoriaRepository repository;
    private final CategoriaMapper mapper;

    public CategoriaService(CategoriaRepository repository, CategoriaMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public CategoriaResponse registrarCategoria(CategoriaRequest request) {
        Categoria categoria = mapper.aEntidad(request);
        Categoria guardada = repository.save(categoria);
        return mapper.aRespuesta(guardada);
    }

    public CategoriaResponse consultarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::aRespuesta)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada con el ID: " + id));
    }
}