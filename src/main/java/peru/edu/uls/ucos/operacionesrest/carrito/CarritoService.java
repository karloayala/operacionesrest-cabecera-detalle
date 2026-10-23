package peru.edu.uls.ucos.operacionesrest.carrito;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import peru.edu.uls.ucos.operacionesrest.cliente.Cliente;
import peru.edu.uls.ucos.operacionesrest.cliente.ClienteRepository;
import peru.edu.uls.ucos.operacionesrest.detallecarrito.DetalleCarritoService;
import peru.edu.uls.ucos.operacionesrest.excepciones.CarritoNoEncontradoException;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoNoEncontradoException;

@Service
public class CarritoService {

    private final CarritoRepository repository;
    private final ClienteRepository clienteRepository;
    private final CarritoMapper mapper;
    private final DetalleCarritoService detalleCarritoService;

    public CarritoService(CarritoRepository repository, ClienteRepository clienteRepository, CarritoMapper mapper,
                          DetalleCarritoService detalleCarritoService) {
        this.repository = repository;
        this.clienteRepository = clienteRepository;
        this.mapper = mapper;
        this.detalleCarritoService = detalleCarritoService;
    }
    
    @Transactional
    public CarritoResponse registrarCarrito(CarritoRequest request) {
        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con el ID: " + request.clienteId()));

        Carrito nuevoCarrito = mapper.aEntidad(request, cliente);
        if (request.detalles() != null) {
            request.detalles().forEach(detalle -> nuevoCarrito.agregarDetalle(
                    detalleCarritoService.crearDetalle(nuevoCarrito, detalle)));
        }
        Carrito guardado = repository.save(nuevoCarrito);
        return mapper.aRespuesta(guardado);
    }

    public CarritoResponse consultarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::aRespuesta)
                .orElseThrow(() -> new RecursoNoEncontradoException("Carrito no encontrado con el ID: " + id));
    }

    public CarritoResponse consultarPorDocumentoCliente(String documento) {
        return repository.buscarCarritoActivoPorDocumentoCliente(documento)
                .map(mapper::aRespuesta)
                .orElseThrow(() -> new CarritoNoEncontradoException(
                        "No se encontró un carrito para el cliente con documento: " + documento));
    }
}