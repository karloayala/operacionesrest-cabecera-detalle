package peru.edu.uls.ucos.operacionesrest.direccion;

import org.springframework.stereotype.Service;

import peru.edu.uls.ucos.operacionesrest.cliente.Cliente;
import peru.edu.uls.ucos.operacionesrest.cliente.ClienteRepository;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoNoEncontradoException;

@Service
public class DireccionService {

    private final DireccionRepository repository;
    private final ClienteRepository clienteRepository;
    private final DireccionMapper mapper;

    public DireccionService(DireccionRepository repository, ClienteRepository clienteRepository, DireccionMapper mapper) {
        this.repository = repository;
        this.clienteRepository = clienteRepository;
        this.mapper = mapper;
    }
    public DireccionResponse registrarDireccion(DireccionRequest request) {
        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con el ID: " + request.clienteId()));

        Direccion nuevaDireccion = mapper.aEntidad(request, cliente);
        Direccion guardada = repository.save(nuevaDireccion);
        return mapper.aRespuesta(guardada);
    }
    
    public DireccionResponse consultarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::aRespuesta)
                .orElseThrow(() -> new RecursoNoEncontradoException("Dirección no encontrada con el ID: " + id));
    }
}