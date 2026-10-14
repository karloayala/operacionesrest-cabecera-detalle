package peru.edu.uls.ucos.operacionesrest.detallepedido;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import peru.edu.uls.ucos.operacionesrest.excepciones.DetallePedidoNoEncontradoException;
import peru.edu.uls.ucos.operacionesrest.excepciones.PedidoNoEncontradoException;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoNoEncontradoException;
import peru.edu.uls.ucos.operacionesrest.pedido.Pedido;
import peru.edu.uls.ucos.operacionesrest.pedido.PedidoRepository;
import peru.edu.uls.ucos.operacionesrest.producto.Producto;
import peru.edu.uls.ucos.operacionesrest.producto.ProductoRepository;

@Service
public class DetallePedidoService {

    private final DetallePedidoRepository detalleRepository;
    private final DetallePedidoMapper detalleMapper;
    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;

    public DetallePedidoService(DetallePedidoRepository detalleRepository,
                                DetallePedidoMapper detalleMapper,
                                PedidoRepository pedidoRepository,
                                ProductoRepository productoRepository) {
        this.detalleRepository = detalleRepository;
        this.detalleMapper = detalleMapper;
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional
    public DetallePedidoResponse agregarDetalle(Long pedidoId, DetallePedidoRequest request) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new PedidoNoEncontradoException(
                        "Pedido no encontrado con ID: " + pedidoId));

        DetallePedido detalle = agregarDetalleA(pedido, request);

        pedidoRepository.save(pedido);
        return detalleMapper.aRespuesta(detalle);
    }

    public DetallePedido agregarDetalleA(Pedido pedido, DetallePedidoRequest request) {
        if (request.cantidad() == null || request.cantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad del detalle debe ser mayor que cero.");
        }

        Producto producto = productoRepository.findById(request.productoId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Producto no encontrado con ID: " + request.productoId()));

        Double precioUnitario = request.precioUnitario() != null
                ? request.precioUnitario()
                : producto.getPrecio();

        if (precioUnitario == null || precioUnitario < 0) {
            throw new IllegalArgumentException(
                    "El precio unitario debe ser mayor o igual que cero.");
        }

        reducirStock(producto, request.cantidad());

        DetallePedido detalle = new DetallePedido(
                pedido, producto,
                request.cantidad(), precioUnitario);

        pedido.agregarDetalle(detalle);

        double subtotal = request.cantidad() * precioUnitario;
        double totalActual = pedido.getTotal() == null
                ? 0.0
                : pedido.getTotal();

        pedido.setTotal(totalActual + subtotal);
        return detalle;
    }

    
    @Transactional
    public void reducirStock(Producto producto, Integer cantidad) {
        if (producto.getStock() == null || producto.getStock() < cantidad) {
            throw new IllegalArgumentException("Stock insuficiente para el producto: " + producto.getNombre());
        }

        producto.setStock(producto.getStock() - cantidad);
        productoRepository.save(producto);
    }

    @Transactional(readOnly = true)
    public DetallePedidoResponse consultarPorId(Long id) {
        return detalleRepository.findById(id)
                .map(detalleMapper::aRespuesta)
                .orElseThrow(() -> new DetallePedidoNoEncontradoException(
                        "Detalle de pedido no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<DetallePedidoResponse> listarPorPedido(Long pedidoId) {
        if (!pedidoRepository.existsById(pedidoId)) {
            throw new PedidoNoEncontradoException("Pedido no encontrado con ID: " + pedidoId);
        }
        return detalleRepository.findByPedidoId(pedidoId)
                .stream()
                .map(detalleMapper::aRespuesta)
                .toList();
    }
}
