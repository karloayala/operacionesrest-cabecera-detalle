package peru.edu.uls.ucos.operacionesrest.pedido;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import peru.edu.uls.ucos.operacionesrest.cliente.Cliente;
import peru.edu.uls.ucos.operacionesrest.cliente.ClienteRepository;
import peru.edu.uls.ucos.operacionesrest.detallepedido.DetallePedido;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoDuplicadoException;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoNoEncontradoException;
import peru.edu.uls.ucos.operacionesrest.producto.Producto;
import peru.edu.uls.ucos.operacionesrest.producto.ProductoRepository;

import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PedidoMapper pedidoMapper;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                         PedidoMapper pedidoMapper,
                         ClienteRepository clienteRepository,
                         ProductoRepository productoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoMapper = pedidoMapper;
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public PedidoResponse consultarPedidoPorId(Long id) {
        return pedidoRepository.findById(id)
                .map(pedidoMapper::aRespuesta)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado con ID: " + id));
    }

    @Transactional
    public PedidoResponse registrarProductoNuevo(PedidoRequest request) {
        if (pedidoRepository.existsByNumeroPedido(request.numeroPedido())) {
            throw new RecursoDuplicadoException("El pedido con número " + request.numeroPedido() + " ya existe.");
        }
        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con ID: " + request.clienteId()));

        Pedido nuevoPedido = pedidoMapper.aEntidad(request, cliente);

        double totalPedido = 0.0;

        if (request.detalles() != null && !request.detalles().isEmpty()) {
            for (var detReq : request.detalles()) {
                Producto producto = productoRepository.findById(detReq.productoId())
                        .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con ID: " + detReq.productoId()));

                Double precioUnitario = detReq.precioUnitario();

                DetallePedido detalle = new DetallePedido(nuevoPedido, producto, detReq.cantidad(), precioUnitario);
                nuevoPedido.agregarDetalle(detalle);

                if (precioUnitario != null) {
                    totalPedido += detReq.cantidad() * precioUnitario;
                }
            }
        }

        nuevoPedido.setTotal(totalPedido);
        Pedido pedidoGuardado = pedidoRepository.save(nuevoPedido);

        return pedidoMapper.aRespuesta(pedidoGuardado);
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> consultarPorEstado(String estado) {
        return pedidoRepository.findByEstadoIgnoreCase(estado)
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
}