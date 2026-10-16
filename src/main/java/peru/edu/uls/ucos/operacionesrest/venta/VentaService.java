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
import peru.edu.uls.ucos.operacionesrest.ventadetalle.VentaDetalle;
import peru.edu.uls.ucos.operacionesrest.ventadetalle.VentaDetalleMapper;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final VentaMapper ventaMapper;
    private final ClienteRepository clienteRepository;
    private final PedidoRepository pedidoRepository;
    private final VentaDetalleMapper ventaDetalleMapper;

    public VentaService(VentaRepository ventaRepository,
                       VentaMapper ventaMapper,
                       ClienteRepository clienteRepository,
                       PedidoRepository pedidoRepository,
                       VentaDetalleMapper ventaDetalleMapper) {
        this.ventaRepository = ventaRepository;
        this.ventaMapper = ventaMapper;
        this.clienteRepository = clienteRepository;
        this.pedidoRepository = pedidoRepository;
        this.ventaDetalleMapper = ventaDetalleMapper;
    }

    @Transactional(readOnly = true)
    public VentaResponse consultarVentaPorId(Long id) {
        return ventaRepository.findById(id)
                .map(ventaMapper::aRespuesta)
                .orElseThrow(() -> new RecursoNoEncontradoException("Venta no encontrada con ID: " + id));
    }

    @Transactional
    public VentaResponse confirmarPedido(Long pedidoId) {
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
        }

        pedido.setVenta(venta);
        pedido.setEstado(EstadoPedido.CONFIRMADO);

        Venta ventaGuardada = ventaRepository.save(venta);

        return ventaMapper.aRespuesta(ventaGuardada);
    }

    @Transactional(readOnly = true)
    public List<VentaResponse> consultarPorEstado(String estado) {
        return ventaRepository.findByEstadoIgnoreCase(estado)
                .stream()
                .map(ventaMapper::aRespuesta)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<VentaResponse> consultarPorCliente(Long clienteId) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new RecursoNoEncontradoException("Cliente no encontrado con ID: " + clienteId);
        }
        return ventaRepository.findByClienteId(clienteId)
                .stream()
                .map(ventaMapper::aRespuesta)
                .toList();
    }
}
