package peru.edu.uls.ucos.operacionesrest.detallecompra;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import peru.edu.uls.ucos.operacionesrest.compra.Compra;
import peru.edu.uls.ucos.operacionesrest.compra.CompraRepository;
import peru.edu.uls.ucos.operacionesrest.producto.Producto;
import peru.edu.uls.ucos.operacionesrest.producto.ProductoRepository;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoNoEncontradoException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DetalleCompraService {

    private final DetalleCompraRepository repository;
    private final CompraRepository compraRepository;
    private final ProductoRepository productoRepository;
    private final DetalleCompraMapper mapper;

    public DetalleCompraService(DetalleCompraRepository repository, 
                                CompraRepository compraRepository, 
                                ProductoRepository productoRepository, 
                                DetalleCompraMapper mapper) {
        this.repository = repository;
        this.compraRepository = compraRepository;
        this.productoRepository = productoRepository;
        this.mapper = mapper;
    }

    @Transactional
    public DetalleCompraResponse registrarDetalle(DetalleCompraRequest request) {
        Compra compra = compraRepository.findById(request.compraId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Compra no encontrada con el ID: " + request.compraId()));
        
        Producto producto = productoRepository.findById(request.productoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con el ID: " + request.productoId()));

        // Buena práctica: Actualizar el inventario automáticamente al ingresar nueva mercadería
        Integer stockActual = producto.getStock() == null ? 0 : producto.getStock();
        producto.setStock(stockActual + request.cantidad());
        productoRepository.save(producto);

        DetalleCompra detalle = mapper.aEntidad(request, compra, producto);
        DetalleCompra guardado = repository.save(detalle);
        return mapper.aRespuesta(guardado);
    }

    @Transactional(readOnly = true)
    public DetalleCompraResponse consultarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::aRespuesta)
                .orElseThrow(() -> new RecursoNoEncontradoException("Detalle de compra no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<DetalleCompraResponse> consultarPorCompra(Long compraId) {
        if (!compraRepository.existsById(compraId)) {
            throw new RecursoNoEncontradoException("Compra no encontrada con ID: " + compraId);
        }
        return repository.findByCompraId(compraId).stream()
                .map(mapper::aRespuesta)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DetalleCompraResponse> consultarPorProducto(Long productoId) {
        if (!productoRepository.existsById(productoId)) {
            throw new RecursoNoEncontradoException("Producto no encontrado con ID: " + productoId);
        }
        return repository.findByProductoId(productoId).stream()
                .map(mapper::aRespuesta)
                .collect(Collectors.toList());
    }
}