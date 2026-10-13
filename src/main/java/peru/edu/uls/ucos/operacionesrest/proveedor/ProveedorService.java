package peru.edu.uls.ucos.operacionesrest.proveedor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoDuplicadoException;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoNoEncontradoException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProveedorService {
    
    private final ProveedorRepository repository;
    private final ProveedorMapper mapper;

    public ProveedorService(ProveedorRepository repository, ProveedorMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public ProveedorResponse registrarProveedor(ProveedorRequest request) {
        if (repository.existsByNombreIgnoreCase(request.nombre())) {
            throw new RecursoDuplicadoException("Ya existe un proveedor registrado con el nombre: " + request.nombre());
        }
        Proveedor proveedor = mapper.aEntidad(request);
        Proveedor guardado = repository.save(proveedor);
        return mapper.aRespuesta(guardado);
    }

    @Transactional(readOnly = true)
    public ProveedorResponse consultarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::aRespuesta)
                .orElseThrow(() -> new RecursoNoEncontradoException("Proveedor no encontrado con el ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<ProveedorResponse> listarTodos() {
        return repository.findAll().stream()
                .map(mapper::aRespuesta)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProveedorResponse> buscarPorNombre(String nombre) {
        List<Proveedor> resultados = repository.findByNombreContainingIgnoreCase(nombre);
        if (resultados.isEmpty()) {
            throw new RecursoNoEncontradoException("No se encontraron proveedores que coincidan con: " + nombre);
        }
        return resultados.stream().map(mapper::aRespuesta).collect(Collectors.toList());
    }
}