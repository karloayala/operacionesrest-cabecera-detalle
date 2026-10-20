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

    @Transactional
    public DetallePedidoResponse agregarDetalle(Long pedidoId, DetallePedidoRequest request) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new PedidoNoEncontradoException(
                        "Pedido no encontrado con ID: " + pedidoId));
        Producto producto = productoRepository.findById(request.productoId())
                .orElseThrow(() -> new PedidoNoEncontradoException(
                        "Producto no encontrado con ID: " + request.productoId()));
                        
        DetallePedido agregar = detalleMapper.aEntidad(pedido, producto,request.cantidad(),request.precioUnitario());
        
        DetallePedido detalle = agregarDetalleA(pedido, agregar);

        pedidoRepository.save(pedido);
        return detalleMapper.aRespuesta(detalle);
    }

    @Transactional
    public DetallePedido agregarDetalleA(Pedido pedido, DetallePedido request) {
        if (request.getCantidad() == null || request.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad del detalle debe ser mayor que cero.");
        }

        Producto producto = productoRepository.findById(request.getProducto().getId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Producto no encontrado con ID: " + request.getProducto()));

        Double precioUnitario = request.getPrecioUnitario() != null
                ? request.getPrecioUnitario()
                : producto.getPrecio();

        if (precioUnitario == null || precioUnitario < 0) {
            throw new IllegalArgumentException(
                    "El precio unitario debe ser mayor o igual que cero.");
        }

        // reducirStock(producto, request.cantidad());

        DetallePedido detalle = new DetallePedido(
                pedido, producto,
                request.getCantidad(), precioUnitario);

        pedido.agregarDetalle(detalle);

        double subtotal = request.getCantidad() * precioUnitario;
        double totalActual = pedido.getTotal() == null
                ? 0.0
                : pedido.getTotal();

        pedido.setTotal(totalActual + subtotal);
        return detalle;
    }

}
