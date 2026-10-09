package peru.edu.uls.ucos.operacionesrest.detallecarrito;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import peru.edu.uls.ucos.operacionesrest.carrito.Carrito;
import peru.edu.uls.ucos.operacionesrest.carrito.CarritoRepository;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoNoEncontradoException;
import peru.edu.uls.ucos.operacionesrest.producto.Producto;
import peru.edu.uls.ucos.operacionesrest.producto.ProductoRepository;

@Service
public class DetalleCarritoService {

    private final DetalleCarritoRepository detalleRepository;
    private final DetalleCarritoMapper detalleMapper;
    private final CarritoRepository carritoRepository;
    private final ProductoRepository productoRepository;

    public DetalleCarritoService(DetalleCarritoRepository detalleRepository,
                                DetalleCarritoMapper detalleMapper,
                                CarritoRepository carritoRepository,
                                ProductoRepository productoRepository) {
        this.detalleRepository = detalleRepository;
        this.detalleMapper = detalleMapper;
        this.carritoRepository = carritoRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional
    public DetalleCarritoResponse agregarDetalle(DetalleCarritoRequest request) {
        if (request.cantidad() == null || request.cantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad del detalle del carrito debe ser mayor que cero.");
        }
        if (request.precioUnitario() == null || request.precioUnitario() < 0) {
            throw new IllegalArgumentException("El precio unitario del detalle del carrito debe ser mayor o igual que cero.");
        }

        Carrito carrito = carritoRepository.findById(request.carritoId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Carrito no encontrado con ID: " + request.carritoId()));

        Producto producto = productoRepository.findById(request.productoId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Producto no encontrado con ID: " + request.productoId()));

        DetalleCarrito detalle = detalleMapper.aEntidad(carrito, producto, request);
        DetalleCarrito detalleGuardado = detalleRepository.save(detalle);

        return detalleMapper.aRespuesta(detalleGuardado);
    }

    @Transactional(readOnly = true)
    public DetalleCarritoResponse consultarPorId(Long id) {
        return detalleRepository.findById(id)
                .map(detalleMapper::aRespuesta)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Detalle de carrito no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<DetalleCarritoResponse> listarPorCarrito(Long carritoId) {
        if (!carritoRepository.existsById(carritoId)) {
            throw new RecursoNoEncontradoException("Carrito no encontrado con ID: " + carritoId);
        }
        return detalleRepository.findByCarritoId(carritoId)
                .stream()
                .map(detalleMapper::aRespuesta)
                .toList();
    }
}
