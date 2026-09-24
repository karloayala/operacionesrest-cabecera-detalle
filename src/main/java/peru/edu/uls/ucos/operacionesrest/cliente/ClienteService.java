package peru.edu.uls.ucos.operacionesrest.cliente;

import org.springframework.stereotype.Service;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoDuplicadoException;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoNoEncontradoException;

@Service
public class ClienteService {

    private final ClienteRepository repository;
    private final ClienteMapper mapper;

    public ClienteService(ClienteRepository repository, ClienteMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public ClienteResponse registrarClienteNuevo(ClienteRequest request) {
        if (repository.existsByDocumento(request.documento())) {
            throw new RecursoDuplicadoException("Ya existe un cliente registrado con el documento: " + request.documento());
        }
        Cliente nuevoCliente = mapper.aEntidad(request);
        Cliente clienteGuardado = repository.save(nuevoCliente);
        return mapper.aRespuesta(clienteGuardado);
    }

    public ClienteResponse consultarClientePorId(Long id) {
        return repository.findById(id)
                .map(mapper::aRespuesta)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con el ID: " + id));
    }

    public ClienteResponse consultarClientePorDocumento(String documento) {
        return repository.findByDocumento(documento)
                .map(mapper::aRespuesta)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con el documento: " + documento));
    }
}