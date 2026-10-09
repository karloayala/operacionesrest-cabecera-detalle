package peru.edu.uls.ucos.operacionesrest.carrito;

import org.springframework.stereotype.Service;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoNoEncontradoException;

@Service
public class CarritoService {

    private final CarritoRepository repository;
    private final CarritoMapper mapper;

    public CarritoService(CarritoRepository repository, CarritoMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public CarritoResponse consultarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::aRespuesta)
                .orElseThrow(() -> new RecursoNoEncontradoException("Carrito no encontrado con el ID: " + id));
    }
}