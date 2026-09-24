package peru.edu.uls.ucos.operacionesrest.producto;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoDuplicadoException;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoNoEncontradoException;
import peru.edu.uls.ucos.operacionesrest.excepciones.StockInsuficienteException;

@Service
public class ProductoService {

    private final ProductoRepository repository;
    private final ProductoMapper mapper;

    public ProductoService(ProductoRepository repository, ProductoMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public ProductoResponse registrarProductoNuevo(ProductoRequest request) {
        if (repository.existsByNombreIgnoreCase(request.nombre())) {
            throw new RecursoDuplicadoException("Ya existe un producto registrado con el nombre: " + request.nombre());
        }
        Producto nuevoProducto = mapper.aEntidad(request);
        Producto productoGuardado = repository.save(nuevoProducto);
        return mapper.aRespuesta(productoGuardado);
    }

    public ProductoResponse consultarProductoPorId(Long id) {
        return repository.findById(id)
                .map(mapper::aRespuesta)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con el ID: " + id));
    }

    public List<ProductoResponse> consultarProductoPorMarca(String marca) {
        List<Producto> productos = repository.findByMarcaIgnoreCase(marca);
        if (productos.isEmpty()) {
            throw new RecursoNoEncontradoException("No se encontraron productos de la marca: " + marca);
        }
        return productos.stream().map(mapper::aRespuesta).toList();
    }

    /**
     * Reduce el stock del producto identificado por {@code productoId} en {@code cantidad} unidades.
     * Lanza {@link StockInsuficienteException} si la cantidad solicitada excede el stock disponible.
     * Devuelve la entidad Producto actualizada (con el stock ya reducido y persistido).
     */
    @Transactional
    public Producto reducirStock(Long productoId, Integer cantidad) {
        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a reducir debe ser mayor que cero.");
        }
        Producto producto = repository.findById(productoId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Producto no encontrado con el ID: " + productoId));
        Integer stockActual = producto.getStock() == null ? 0 : producto.getStock();
        if (stockActual < cantidad) {
            throw new StockInsuficienteException(
                    "Stock insuficiente para el producto '" + producto.getNombre()
                            + "'. Stock disponible: " + stockActual + ", solicitado: " + cantidad);
        }
        producto.setStock(stockActual - cantidad);
        return repository.save(producto);
    }
}
