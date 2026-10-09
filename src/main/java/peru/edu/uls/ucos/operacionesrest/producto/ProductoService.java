package peru.edu.uls.ucos.operacionesrest.producto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import peru.edu.uls.ucos.operacionesrest.cliente.ClienteRepository;
import peru.edu.uls.ucos.operacionesrest.categoria.Categoria;
import peru.edu.uls.ucos.operacionesrest.categoria.CategoriaRepository;
import peru.edu.uls.ucos.operacionesrest.proveedor.Proveedor;
import peru.edu.uls.ucos.operacionesrest.proveedor.ProveedorRepository;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoDuplicadoException;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoNoEncontradoException;
import peru.edu.uls.ucos.operacionesrest.excepciones.StockInsuficienteException;

@Service
public class ProductoService {

    private final ProductoRepository repository;
    private final ProductoMapper mapper;
    private final ClienteRepository clienteRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProveedorRepository proveedorRepository;

    public ProductoService(ProductoRepository repository,
                           ProductoMapper mapper,
                           ClienteRepository clienteRepository,
                           CategoriaRepository categoriaRepository,
                           ProveedorRepository proveedorRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.clienteRepository = clienteRepository;
        this.categoriaRepository = categoriaRepository;
        this.proveedorRepository = proveedorRepository;
    }

    @Transactional
    public ProductoResponse registrarProductoNuevo(ProductoRequest request) {
        if (repository.existsByNombreIgnoreCase(request.nombre())) {
            throw new RecursoDuplicadoException("Ya existe un producto registrado con el nombre: " + request.nombre());
        }
        
        Categoria categoria = null;
        if (request.categoriaId() != null) {
            categoria = categoriaRepository.findById(request.categoriaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada con ID: " + request.categoriaId()));
        }
        
        Proveedor proveedor = null;
        if (request.proveedorId() != null) {
            proveedor = proveedorRepository.findById(request.proveedorId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Proveedor no encontrado con ID: " + request.proveedorId()));
        }

        Producto nuevoProducto = mapper.aEntidad(request, categoria, proveedor);
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
    
    public List<ProductoResponse> consultarProductosPorCategoria(Long categoriaId) {
        List<Producto> productos = repository.findByCategoriaId(categoriaId);
        return productos.stream().map(mapper::aRespuesta).toList();
    }
    
    public List<ProductoResponse> consultarProductosPorProveedor(Long proveedorId) {
        List<Producto> productos = repository.findByProveedorId(proveedorId);
        return productos.stream().map(mapper::aRespuesta).toList();
    }

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

    @Transactional(readOnly = true)
    public List<ProductoResponse> listarProductosIncluidosEnPedidos() {
        List<Producto> productos = repository.findProductosIncluidosEnPedidos();
        if (productos.isEmpty()) {
            throw new RecursoNoEncontradoException(
                    "No se encontraron productos incluidos en pedidos.");
        }
        return productos.stream().map(mapper::aRespuesta).toList();
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> listarProductosVendidos() {
        List<Producto> productos = repository.findProductosVendidos();
        if (productos.isEmpty()) {
            throw new RecursoNoEncontradoException(
                    "No se encontraron productos que tengan ventas registradas.");
        }
        return productos.stream().map(mapper::aRespuesta).toList();
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> listarProductosVendidosConStockBajo(Integer stockMaximo) {
        if (stockMaximo == null || stockMaximo < 0) {
            throw new IllegalArgumentException(
                    "El valor de stock máximo debe ser un número entero mayor o igual que cero.");
        }
        List<Producto> productos = repository.findProductosVendidosConStockBajo(stockMaximo);
        if (productos.isEmpty()) {
            throw new RecursoNoEncontradoException(
                    "No se encontraron productos vendidos con stock menor o igual a " + stockMaximo + ".");
        }
        return productos.stream().map(mapper::aRespuesta).toList();
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> listarProductosPedidosPorCliente(Long clienteId) {
        validarIdPositivo(clienteId, "clienteId");
        if (!clienteRepository.existsById(clienteId)) {
            throw new RecursoNoEncontradoException(
                    "Cliente no encontrado con ID: " + clienteId);
        }
        List<Producto> productos = repository.findProductosPedidosPorCliente(clienteId);
        if (productos.isEmpty()) {
            throw new RecursoNoEncontradoException(
                    "El cliente con ID " + clienteId + " no tiene productos asociados a pedidos.");
        }
        return productos.stream().map(mapper::aRespuesta).toList();
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> listarProductosVendidosEntreFechas(LocalDateTime fechaInicio,
                                                                     LocalDateTime fechaFin) {
        Objects.requireNonNull(fechaInicio, "La fecha de inicio no puede ser nula.");
        Objects.requireNonNull(fechaFin, "La fecha de fin no puede ser nula.");
        if (fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException(
                    "La fecha de fin no puede ser anterior a la fecha de inicio.");
        }
        List<Producto> productos = repository.findProductosVendidosEntreFechas(fechaInicio, fechaFin);
        if (productos.isEmpty()) {
            throw new RecursoNoEncontradoException(
                    "No se encontraron productos vendidos entre "
                            + fechaInicio + " y " + fechaFin + ".");
        }
        return productos.stream().map(mapper::aRespuesta).toList();
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> listarProductosPedidosYVendidos() {
        List<Producto> productos = repository.findProductosPedidosYVendidos();
        if (productos.isEmpty()) {
            throw new RecursoNoEncontradoException(
                    "No se encontraron productos incluidos simultáneamente en pedidos y ventas.");
        }
        return productos.stream().map(mapper::aRespuesta).toList();
    }

    @Transactional(readOnly = true)
    public List<ProductoEstadisticaResponse> listarEstadisticasProductosVendidosACliente(Long clienteId) {
        validarIdPositivo(clienteId, "clienteId");
        if (!clienteRepository.existsById(clienteId)) {
            throw new RecursoNoEncontradoException(
                    "Cliente no encontrado con ID: " + clienteId);
        }
        List<ProductoEstadisticaProjection> filas =
                repository.findEstadisticasProductosVendidosACliente(clienteId);
        if (filas.isEmpty()) {
            throw new RecursoNoEncontradoException(
                    "El cliente con ID " + clienteId + " no tiene productos vendidos registrados.");
        }
        return filas.stream().map(mapper::aRespuestaEstadistica).toList();
    }

    @Transactional(readOnly = true)
    public List<ProductoEstadisticaResponse> listarTop5ProductosMasVendidos() {
        List<ProductoEstadisticaProjection> filas = repository.findTop5ProductosMasVendidos();
        if (filas.isEmpty()) {
            throw new RecursoNoEncontradoException(
                    "No se han registrado ventas todavía; no es posible calcular el Top 5.");
        }
        return filas.stream().map(mapper::aRespuestaEstadistica).toList();
    }

    private void validarIdPositivo(Long id, String nombreCampo) {
        if (id == null) {
            throw new IllegalArgumentException(
                    "El campo '" + nombreCampo + "' no puede ser nulo.");
        }
        if (id <= 0) {
            throw new IllegalArgumentException(
                    "El campo '" + nombreCampo + "' debe ser un número positivo (> 0).");
        }
    }
}