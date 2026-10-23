package peru.edu.uls.ucos.operacionesrest.pedido;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import peru.edu.uls.ucos.operacionesrest.carrito.Carrito;
import peru.edu.uls.ucos.operacionesrest.carrito.CarritoRepository;
import peru.edu.uls.ucos.operacionesrest.cliente.ClienteRepository;
import peru.edu.uls.ucos.operacionesrest.detallecarrito.DetalleCarrito;
import peru.edu.uls.ucos.operacionesrest.detallepedido.DetallePedido;
import peru.edu.uls.ucos.operacionesrest.detallepedido.DetallePedidoMapper;
import peru.edu.uls.ucos.operacionesrest.detallepedido.DetallePedidoService;
import peru.edu.uls.ucos.operacionesrest.excepciones.PedidoNoEncontradoException;
// import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoDuplicadoException;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoNoEncontradoException;


import java.time.LocalDateTime;
import java.util.List;

@Service
public class PedidoService {

    private final CarritoRepository carritoRespository;
    private final PedidoRepository pedidoRepository;
    private final PedidoMapper pedidoMapper;
    private final ClienteRepository clienteRepository;
    private final DetallePedidoMapper detallePedidoMapper;
    private final DetallePedidoService detallePedidoService;

    public PedidoService(PedidoRepository pedidoRepository,
                         PedidoMapper pedidoMapper,
                         ClienteRepository clienteRepository,
                         CarritoRepository carritoRepository,
                         DetallePedidoMapper detallePedidoMapper,
                         DetallePedidoService detallePedidoService
                        ) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoMapper = pedidoMapper;
        this.clienteRepository = clienteRepository;
        this.carritoRespository = carritoRepository;
        this.detallePedidoMapper = detallePedidoMapper;
        this.detallePedidoService = detallePedidoService;
    }

    @Transactional(readOnly = true)
    public PedidoResponse consultarPedidoPorId(Long id) {
        return pedidoRepository.buscarCompletoPorId(id)
                .map(pedidoMapper::aRespuesta)
                .orElseThrow(() -> new PedidoNoEncontradoException("Pedido no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> consultarPorDocumentoCliente(String documento) {
        return pedidoRepository.buscarPorDocumentoCliente(documento)
                .stream()
                .map(pedidoMapper::aRespuesta)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> consultarTodosConDetallesYProductos() {
        return pedidoRepository.buscarTodosConDetallesYProductos()
                .stream()
                .map(pedidoMapper::aRespuesta)
                .toList();
    }

    
    @Transactional(readOnly = true)
    public List<PedidoResponse> consultarPorEstado(String estado) {
        EstadoPedido estadoEnum = EstadoPedido.valueOf(estado.toUpperCase());
        return pedidoRepository.findByEstado(estadoEnum)
                .stream()
                .map(pedidoMapper::aRespuesta)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> consultarPorCliente(Long clienteId) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new RecursoNoEncontradoException("Cliente no encontrado con ID: " + clienteId);
        }
        return pedidoRepository.findByClienteId(clienteId)
                .stream()
                .map(pedidoMapper::aRespuesta)
                .toList();
    }

    // @Transactional
    // public PedidoResponse crearPedido(PedidoRequest request) {

    //     if (request.detalles() == null || request.detalles().isEmpty()) {
    //         throw new IllegalArgumentException("El pedido debe contener al menos un producto.");
    //     }

    //     Cliente cliente = clienteRepository.findById(request.clienteId())
    //             .orElseThrow(() -> new RecursoNoEncontradoException(
    //                     "Cliente no encontrado con ID: " + request.clienteId()));

    //     Pedido nuevoPedido = pedidoMapper.aEntidad(request, cliente);

    //     nuevoPedido.setFecha(LocalDateTime.now());
    //     nuevoPedido.setEstado(EstadoPedido.PENDIENTE);
    //     nuevoPedido.setTotal(0.0);

    //     for (var detReq : request.detalles()) {
    //         detallePedidoService.agregarDetalleA(nuevoPedido, detReq);
    //     }

    //     Pedido pedidoGuardado = pedidoRepository.save(nuevoPedido);

    //     return pedidoMapper.aRespuesta(pedidoGuardado);
    // }

    @Transactional
    public PedidoResponse crearPedido(Long carritoId) {
        Carrito carrito = carritoRespository.findById(carritoId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Carrito no encontrado con ID: " + carritoId));

        if (carrito.getDetalles() == null || carrito.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("El pedido debe contener al menos un producto.");}
        
        if (!"ACTIVO".equals(carrito.getEstado())) {
            throw new IllegalArgumentException("Solo se pueden confirmar carrito activo.");}

        Pedido pedido = new Pedido(
            0.0,
            carrito.getCliente()
        );

        pedido.setFecha(LocalDateTime.now());
    
        for (DetalleCarrito detallecarrito : carrito.getDetalles()) {

            DetallePedido detallePedido = detallePedidoMapper.aEntidad(
                    pedido,
                    detallecarrito.getProducto(),
                    detallecarrito.getCantidad(),
                    detallecarrito.getPrecioUnitario()
            );

            detallePedidoService.agregarDetalleA(pedido, detallePedido);
        }

        pedido.setEstado(EstadoPedido.PENDIENTE);

        Pedido pedidoGuardado = pedidoRepository.save(pedido);
        return pedidoMapper.aRespuesta(pedidoGuardado);
    }
}