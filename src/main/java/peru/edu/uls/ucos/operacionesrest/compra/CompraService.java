package peru.edu.uls.ucos.operacionesrest.compra;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import peru.edu.uls.ucos.operacionesrest.proveedor.Proveedor;
import peru.edu.uls.ucos.operacionesrest.proveedor.ProveedorRepository;
import peru.edu.uls.ucos.operacionesrest.detallecompra.DetalleCompra;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoNoEncontradoException;
import peru.edu.uls.ucos.operacionesrest.producto.Producto;
import peru.edu.uls.ucos.operacionesrest.producto.ProductoRepository;
import peru.edu.uls.ucos.operacionesrest.producto.ProductoService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompraService {

    private final CompraRepository repository;
    private final ProveedorRepository proveedorRepository;
    private final CompraMapper mapper;
    private final ProductoService productoService;
    private final ProductoRepository productoRepository;

    public CompraService(CompraRepository repository, 
                         ProveedorRepository proveedorRepository, 
                         CompraMapper mapper,
                         ProductoService productoService,
                         ProductoRepository productoRepository) {
        this.repository = repository;
        this.proveedorRepository = proveedorRepository;
        this.mapper = mapper;
        this.productoService = productoService;
        this.productoRepository = productoRepository;
    }

    @Transactional
    public CompraResponse registrarCompra(CompraRequest request) {
        Proveedor proveedor = proveedorRepository.findById(request.proveedorId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Proveedor no encontrado con el ID: " + request.proveedorId()));
        
        Compra compra = new Compra();
        compra.setProveedor(proveedor);
        compra.setEstado("RECIBIDO");
        
        double totalCalculado = 0.0;

        for (DetalleCompraRequest detReq : request.detalles()) {
            Producto producto = productoRepository.findById(detReq.productoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));

            DetalleCompra detalle = new DetalleCompra();
            detalle.setProducto(producto);
            detalle.setCantidad(detReq.cantidad());
            detalle.setPrecioUnitario(detReq.precioUnitario());
            
            compra.agregarDetalle(detalle);

            totalCalculado += (detReq.cantidad() * detReq.precioUnitario());

            productoService.aumentarStock(producto.getId(), detReq.cantidad());
        }

        compra.setTotal(totalCalculado);
        
        Compra guardada = repository.save(compra);
        return mapper.aRespuesta(guardada);
    }

    @Transactional(readOnly = true)
    public CompraResponse consultarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::aRespuesta)
                .orElseThrow(() -> new RecursoNoEncontradoException("Compra no encontrada con ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<CompraResponse> consultarPorProveedor(Long proveedorId) {
        if (!proveedorRepository.existsById(proveedorId)) {
            throw new RecursoNoEncontradoException("Proveedor no encontrado con ID: " + proveedorId);
        }
        return repository.findByProveedorId(proveedorId)
                .stream()
                .map(mapper::aRespuesta)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CompraResponse> consultarPorEstado(String estado) {
        return repository.findByEstadoIgnoreCase(estado)
                .stream()
                .map(mapper::aRespuesta)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CompraResponse> consultarPorRangoDeFechas(LocalDateTime inicio, LocalDateTime fin) {
        if (fin.isBefore(inicio)) {
            throw new IllegalArgumentException("La fecha final no puede ser menor a la inicial");
        }
        return repository.findByFechaBetween(inicio, fin)
                .stream()
                .map(mapper::aRespuesta)
                .collect(Collectors.toList());
    }
}