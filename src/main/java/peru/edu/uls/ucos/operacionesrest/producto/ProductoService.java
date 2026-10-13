package peru.edu.uls.ucos.operacionesrest.producto;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import peru.edu.uls.ucos.operacionesrest.categoria.Categoria;
import peru.edu.uls.ucos.operacionesrest.categoria.CategoriaRepository;
import peru.edu.uls.ucos.operacionesrest.proveedor.ProveedorRepository;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoDuplicadoException;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoNoEncontradoException;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository repository;
    private final ProductoMapper mapper;
    private final CategoriaRepository categoriaRepository;
    private final ProveedorRepository proveedorRepository;

    public ProductoService(ProductoRepository repository,
                           ProductoMapper mapper,
                           CategoriaRepository categoriaRepository,
                           ProveedorRepository proveedorRepository) {
        this.repository = repository;
        this.mapper = mapper;
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

        Producto nuevoProducto = mapper.aEntidad(request, categoria);
        Producto productoGuardado = repository.save(nuevoProducto);
        return mapper.aRespuesta(productoGuardado);
    }

    @Transactional(readOnly = true)
    public ProductoResponse consultarProductoPorId(Long id) {
        return repository.findById(id)
                .map(mapper::aRespuesta)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con el ID: " + id));
    }
    
    @Transactional(readOnly = true)
    public List<ProductoResponse> consultarProductosPorCategoria(Long categoriaId) {
        return repository.findByCategoriaId(categoriaId)
                .stream().map(mapper::aRespuesta).toList();
    }
    
    // Nueva lógica: Ahora busca en el historial de abastecimiento usando el JPQL
    @Transactional(readOnly = true)
    public List<ProductoResponse> consultarProductosPorProveedor(Long proveedorId) {
        if (!proveedorRepository.existsById(proveedorId)) {
            throw new RecursoNoEncontradoException("Proveedor no encontrado con ID: " + proveedorId);
        }
        List<Producto> productos = repository.findProductosAbastecidosPorProveedor(proveedorId);
        return productos.stream().map(mapper::aRespuesta).toList();
    }

    @Transactional(readOnly = true)
    public List<ProductoEstadisticaResponse> listarTop5ProductosMasAbastecidos() {
        List<ProductoEstadisticaProjection> filas = repository.findTop5ProductosMasAbastecidos();
        if (filas.isEmpty()) {
            throw new RecursoNoEncontradoException("No se han registrado compras a proveedores todavía.");
        }
        return filas.stream().map(mapper::aRespuestaEstadistica).toList();
    }

    @Transactional(readOnly = true)
    public List<ProductoEstadisticaResponse> listarEstadisticasPorProveedor(Long proveedorId) {
        if (!proveedorRepository.existsById(proveedorId)) {
            throw new RecursoNoEncontradoException("Proveedor no encontrado con ID: " + proveedorId);
        }
        List<ProductoEstadisticaProjection> filas = repository.findEstadisticasAbastecimientoPorProveedor(proveedorId);
        if (filas.isEmpty()) {
            throw new RecursoNoEncontradoException("El proveedor no tiene historial de abastecimiento.");
        }
        return filas.stream().map(mapper::aRespuestaEstadistica).toList();
    }
}