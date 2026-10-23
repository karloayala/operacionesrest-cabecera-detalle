package peru.edu.uls.ucos.operacionesrest.venta;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peru.edu.uls.ucos.operacionesrest.cliente.ClienteRepository;
import peru.edu.uls.ucos.operacionesrest.detallepedido.DetallePedido;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoDuplicadoException;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoNoEncontradoException;
import peru.edu.uls.ucos.operacionesrest.pedido.EstadoPedido;
import peru.edu.uls.ucos.operacionesrest.pedido.Pedido;
import peru.edu.uls.ucos.operacionesrest.pedido.PedidoRepository;
import peru.edu.uls.ucos.operacionesrest.producto.ProductoRepository;
import peru.edu.uls.ucos.operacionesrest.ventadetalle.VentaDetalle;
import peru.edu.uls.ucos.operacionesrest.ventadetalle.VentaDetalleMapper;
import peru.edu.uls.ucos.operacionesrest.ventadetalle.VentaDetalleService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class VentaService {

    private final VentaDetalleService ventaDetalleService;
    private final VentaRepository ventaRepository;
    private final VentaMapper ventaMapper;
    private final ClienteRepository clienteRepository;
    private final PedidoRepository pedidoRepository;
    private final VentaDetalleMapper ventaDetalleMapper;
    private final ProductoRepository productoRepository;

    // Mapa para gestionar los candados (locks) por pedido y evitar condiciones de carrera
    private final ConcurrentHashMap<Long, Lock> locksPorPedido = new ConcurrentHashMap<>();

    public VentaService(VentaRepository ventaRepository,
                       VentaMapper ventaMapper,
                       ClienteRepository clienteRepository,
                       PedidoRepository pedidoRepository,
                       VentaDetalleMapper ventaDetalleMapper, 
                       VentaDetalleService ventaDetalleService,
                       ProductoRepository productoRepository)
        {
        this.ventaRepository = ventaRepository;
        this.ventaMapper = ventaMapper;
        this.clienteRepository = clienteRepository;
        this.pedidoRepository = pedidoRepository;
        this.ventaDetalleMapper = ventaDetalleMapper;
        this.ventaDetalleService = ventaDetalleService;
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public VentaResponse consultarVentaPorId(Long id) {
        return ventaRepository.findById(id)
                .map(ventaMapper::aRespuesta)
                .orElseThrow(() -> new RecursoNoEncontradoException("Venta no encontrada con ID: " + id));
    }

    @Transactional
    public VentaResponse confirmarPedido(Long pedidoId) {
        // 1. Obtener o crear un Lock específico para este ID de pedido
        Lock lock = locksPorPedido.computeIfAbsent(pedidoId, k -> new ReentrantLock());
        
        lock.lock(); // Bloquear acceso a otros hilos para este mismo pedido
        try {
            Pedido pedido = pedidoRepository.findById(pedidoId)
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Pedido no encontrado con ID: " + pedidoId));

            if (pedido.getVenta() != null) {
                throw new RecursoDuplicadoException("El pedido ya fue confirmado con una venta.");
            }

            if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
                throw new IllegalArgumentException("Solo se pueden confirmar pedidos pendientes.");
            }

            Venta venta = new Venta(
                    pedido.getTotal(),
                    EstadoVenta.CONFIRMADA,
                    pedido.getCliente()
            );

            venta.setFecha(LocalDateTime.now());
            venta.setPedido(pedido);

            for (DetallePedido detallePedido : pedido.getDetalles()) {

                VentaDetalle detalleVenta = ventaDetalleMapper.aEntidad(
                        venta,
                        detallePedido.getProducto(),
                        detallePedido.getCantidad(),
                        detallePedido.getPrecioUnitario()
                );

                venta.agregarDetalle(detalleVenta);
                ventaDetalleService.reducirStock(detalleVenta.getProducto(), detalleVenta.getCantidad());
            }

            pedido.setVenta(venta);
            pedido.setEstado(EstadoPedido.CONFIRMADO);

            Venta ventaGuardada = ventaRepository.save(venta);

            return ventaMapper.aRespuesta(ventaGuardada);
        } finally {
            lock.unlock(); // Siempre liberar el candado al terminar
            locksPorPedido.remove(pedidoId); // Limpiar memoria
        }
    }

    @Transactional(readOnly = true)
    public List<VentaResponse> consultarPorEstado(String estado) {
        EstadoVenta estadoEnum = EstadoVenta.valueOf(estado.toUpperCase());
        return ventaRepository.findByEstado(estadoEnum)
                .stream()
                .map(ventaMapper::aRespuesta)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<VentaResponse> consultarPorCliente(Long clienteId) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new RecursoNoEncontradoException("Cliente no encontrado con ID: " + clienteId);
        }
        return ventaRepository.buscarPorClienteId(clienteId)
                .stream()
                .map(ventaMapper::aRespuesta)
                .toList();
    }

    // Consulta multi-entidad: busca todas las ventas que contienen un producto específico (venta + detalle_venta + producto).
    @Transactional(readOnly = true)
    public List<VentaResponse> consultarPorProducto(Long productoId) {
        if (!productoRepository.existsById(productoId)) {
            throw new RecursoNoEncontradoException("Producto no encontrado con ID: " + productoId);
        }
        return ventaRepository.buscarPorProducto(productoId)
                .stream()
                .map(ventaMapper::aRespuesta)
                .toList();
    }
}