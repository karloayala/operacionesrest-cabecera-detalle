package peru.edu.uls.ucos.operacionesrest.pedido;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import peru.edu.uls.ucos.operacionesrest.cliente.Cliente;
import peru.edu.uls.ucos.operacionesrest.cliente.ClienteRepository;
import peru.edu.uls.ucos.operacionesrest.detallepedido.DetallePedidoService;
import peru.edu.uls.ucos.operacionesrest.excepciones.PedidoNoEncontradoException;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoNoEncontradoException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PedidoMapper pedidoMapper;
    private final ClienteRepository clienteRepository;
    private final DetallePedidoService detallePedidoService;

    public PedidoService(PedidoRepository pedidoRepository,
                         PedidoMapper pedidoMapper,
                         ClienteRepository clienteRepository,
                         DetallePedidoService detallePedidoService
                        ) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoMapper = pedidoMapper;
        this.clienteRepository = clienteRepository;
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

    @Transactional
    public PedidoResponse crearPedido(PedidoRequest request) {

        if (request.detalles() == null || request.detalles().isEmpty()) {
            throw new IllegalArgumentException("El pedido debe contener al menos un producto.");
        }

        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Cliente no encontrado con ID: " + request.clienteId()));

        Pedido nuevoPedido = pedidoMapper.aEntidad(request, cliente);

        nuevoPedido.setFecha(LocalDateTime.now());
        nuevoPedido.setEstado(EstadoPedido.PENDIENTE);
        nuevoPedido.setTotal(0.0);

        for (var detReq : request.detalles()) {
            detallePedidoService.agregarDetalleA(nuevoPedido, detReq);
        }

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