package peru.edu.uls.ucos.operacionesrest.venta;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peru.edu.uls.ucos.operacionesrest.cliente.Cliente;
import peru.edu.uls.ucos.operacionesrest.cliente.ClienteRepository;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoDuplicadoException;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoNoEncontradoException;
import peru.edu.uls.ucos.operacionesrest.producto.Producto;
import peru.edu.uls.ucos.operacionesrest.producto.ProductoRepository;
import peru.edu.uls.ucos.operacionesrest.producto.ProductoService;
import peru.edu.uls.ucos.operacionesrest.ventadetalle.VentaDetalle;
import peru.edu.uls.ucos.operacionesrest.ventadetalle.VentaDetalleRequest;

import java.util.List;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final VentaMapper ventaMapper;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final ProductoService productoService;

    public VentaService(VentaRepository ventaRepository,
                       VentaMapper ventaMapper,
                       ClienteRepository clienteRepository,
                       ProductoRepository productoRepository,
                       ProductoService productoService) {
        this.ventaRepository = ventaRepository;
        this.ventaMapper = ventaMapper;
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
        this.productoService = productoService;
    }

    @Transactional(readOnly = true)
    public VentaResponse consultarVentaPorId(Long id) {
        return ventaRepository.findById(id)
                .map(ventaMapper::aRespuesta)
                .orElseThrow(() -> new RecursoNoEncontradoException("Venta no encontrada con ID: " + id));
    }

    @Transactional
    public VentaResponse registrarVenta(VentaRequest request) {
        if (ventaRepository.existsByNumeroVenta(request.numeroVenta())) {
            throw new RecursoDuplicadoException("La venta con número " + request.numeroVenta() + " ya existe.");
        }

        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con ID: " + request.clienteId()));

        Venta nuevaVenta = ventaMapper.aEntidad(request, cliente);
        double totalVenta = 0.0;

        if (request.detalles() != null && !request.detalles().isEmpty()) {
            for (var detReq : request.detalles()) {
                Producto producto = productoRepository.findById(detReq.productoId())
                        .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con ID: " + detReq.productoId()));

                Double precioUnitario = detReq.precioUnitario() != null ? detReq.precioUnitario() : producto.getPrecio();
                if (precioUnitario == null || precioUnitario < 0) {
                    throw new IllegalArgumentException("El precio unitario del detalle debe ser mayor o igual que cero.");
                }

                if (detReq.cantidad() == null || detReq.cantidad() <= 0) {
                    throw new IllegalArgumentException("La cantidad del detalle debe ser mayor que cero.");
                }

                productoService.reducirStock(producto.getId(), detReq.cantidad());

                VentaDetalle detalle = new VentaDetalle(nuevaVenta, producto, detReq.cantidad(), precioUnitario);
                nuevaVenta.agregarDetalle(detalle);

                totalVenta += detReq.cantidad() * precioUnitario;
            }
        }

        nuevaVenta.setTotal(totalVenta);
        Venta ventaGuardada = ventaRepository.save(nuevaVenta);
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
