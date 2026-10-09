package peru.edu.uls.ucos.operacionesrest.carrito;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import peru.edu.uls.ucos.operacionesrest.cliente.Cliente;
import peru.edu.uls.ucos.operacionesrest.cliente.ClienteRepository;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoNoEncontradoException;

@Service
public class CarritoService {

    private final CarritoRepository repository;
    private final ClienteRepository clienteRepository;
    private final CarritoMapper mapper;

    public CarritoService(CarritoRepository repository, ClienteRepository clienteRepository, CarritoMapper mapper) {
        this.repository = repository;
        this.clienteRepository = clienteRepository;
        this.mapper = mapper;
    }
    
    public CarritoResponse registrarCarrito(CarritoRequest request) {
        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con el ID: " + request.clienteId()));

        Carrito nuevoCarrito = mapper.aEntidad(request, cliente);
        Carrito guardado = repository.save(nuevoCarrito);
        return mapper.aRespuesta(guardado);
    }

    public CarritoResponse consultarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::aRespuesta)
                .orElseThrow(() -> new RecursoNoEncontradoException("Carrito no encontrado con el ID: " + id));
    }
}