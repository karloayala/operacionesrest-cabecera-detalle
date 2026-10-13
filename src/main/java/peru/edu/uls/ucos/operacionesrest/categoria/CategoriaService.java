package peru.edu.uls.ucos.operacionesrest.categoria;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoDuplicadoException;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoNoEncontradoException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoriaService {
    
    private final CategoriaRepository repository;
    private final CategoriaMapper mapper;

    public CategoriaService(CategoriaRepository repository, CategoriaMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public CategoriaResponse registrarCategoria(CategoriaRequest request) {
        if (repository.existsByNombreIgnoreCase(request.nombre())) {
            throw new RecursoDuplicadoException("Ya existe una categoría registrada con el nombre: " + request.nombre());
        }
        Categoria categoria = mapper.aEntidad(request);
        Categoria guardada = repository.save(categoria);
        return mapper.aRespuesta(guardada);
    }

    @Transactional(readOnly = true)
    public CategoriaResponse consultarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::aRespuesta)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada con el ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listarTodas() {
        return repository.findAll().stream()
                .map(mapper::aRespuesta)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> buscarPorNombre(String nombre) {
        List<Categoria> resultados = repository.findByNombreContainingIgnoreCase(nombre);
        if (resultados.isEmpty()) {
            throw new RecursoNoEncontradoException("No se encontraron categorías que coincidan con: " + nombre);
        }
        return resultados.stream().map(mapper::aRespuesta).collect(Collectors.toList());
    }
}