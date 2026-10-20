package peru.edu.uls.ucos.operacionesrest.ventadetalle;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoNoEncontradoException;
import peru.edu.uls.ucos.operacionesrest.producto.Producto;
import peru.edu.uls.ucos.operacionesrest.producto.ProductoRepository;

@Service
public class VentaDetalleService {

    private final ProductoRepository productoRepository;
    private final VentaDetalleRepository repository;
    private final VentaDetalleMapper mapper;

    public VentaDetalleService(VentaDetalleRepository repository, VentaDetalleMapper mapper, ProductoRepository productoRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public VentaDetalleResponse consultarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::aRespuesta)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Detalle de venta no encontrado con el ID: " + id));
    }

    
    @Transactional
    public void reducirStock(Producto producto, Integer cantidad) {
        if (producto.getStock() == null || producto.getStock() < cantidad) {
            throw new IllegalArgumentException("Stock insuficiente para el producto: " + producto.getNombre());
        }

        producto.setStock(producto.getStock() - cantidad);
        productoRepository.save(producto);
    }
}