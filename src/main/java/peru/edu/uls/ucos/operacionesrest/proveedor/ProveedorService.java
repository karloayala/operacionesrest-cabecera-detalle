package peru.edu.uls.ucos.operacionesrest.proveedor;

import org.springframework.stereotype.Service;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoNoEncontradoException;

@Service
public class ProveedorService {
    
    private final ProveedorRepository repository;
    private final ProveedorMapper mapper;

    public ProveedorService(ProveedorRepository repository, ProveedorMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public ProveedorResponse registrarProveedor(ProveedorRequest request) {
        Proveedor proveedor = mapper.aEntidad(request);
        Proveedor guardado = repository.save(proveedor);
        return mapper.aRespuesta(guardado);
    }

    public ProveedorResponse consultarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::aRespuesta)
                .orElseThrow(() -> new RecursoNoEncontradoException("Proveedor no encontrado con el ID: " + id));
    }
}