package peru.edu.uls.ucos.operacionesrest.producto;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ProductoResponse> registrar(@RequestBody ProductoRequest request) {
        ProductoResponse response = service.registrarProductoNuevo(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> consultarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.consultarProductoPorId(id));
    }

    @GetMapping("/marca/{marca}")
    public ResponseEntity<List<ProductoResponse>> consultarPorMarca(@PathVariable String marca) {
        return ResponseEntity.ok(service.consultarProductoPorMarca(marca));
    }
    
    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<ProductoResponse>> consultarPorCategoria(@PathVariable Long categoriaId) {
        return ResponseEntity.ok(service.consultarProductosPorCategoria(categoriaId));
    }
    
    @GetMapping("/proveedor/{proveedorId}")
    public ResponseEntity<List<ProductoResponse>> consultarPorProveedor(@PathVariable Long proveedorId) {
        return ResponseEntity.ok(service.consultarProductosPorProveedor(proveedorId));
    }

    @GetMapping("/consultas/en-pedidos")
    public ResponseEntity<List<ProductoResponse>> productosIncluidosEnPedidos() {
        return ResponseEntity.ok(service.listarProductosIncluidosEnPedidos());
    }

    @GetMapping("/consultas/vendidos")
    public ResponseEntity<List<ProductoResponse>> productosVendidos() {
        return ResponseEntity.ok(service.listarProductosVendidos());
    }

    @GetMapping("/consultas/vendidos-stock-bajo")
    public ResponseEntity<List<ProductoResponse>> productosVendidosConStockBajo(
            @RequestParam("stockMaximo") Integer stockMaximo) {
        return ResponseEntity.ok(service.listarProductosVendidosConStockBajo(stockMaximo));
    }

    @GetMapping("/consultas/pedidos/cliente/{clienteId}")
    public ResponseEntity<List<ProductoResponse>> productosPedidosPorCliente(
            @PathVariable Long clienteId) {
        return ResponseEntity.ok(service.listarProductosPedidosPorCliente(clienteId));
    }

    @GetMapping("/consultas/vendidos-entre")
    public ResponseEntity<List<ProductoResponse>> productosVendidosEntreFechas(
            @RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam("fin")    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        validarRangoFechas(inicio, fin);
        return ResponseEntity.ok(service.listarProductosVendidosEntreFechas(inicio, fin));
    }

    @GetMapping("/consultas/pedidos-y-ventas")
    public ResponseEntity<List<ProductoResponse>> productosPedidosYVendidos() {
        return ResponseEntity.ok(service.listarProductosPedidosYVendidos());
    }

    @GetMapping("/consultas/estadisticas/cliente/{clienteId}")
    public ResponseEntity<List<ProductoEstadisticaResponse>> estadisticasProductosVendidosACliente(
            @PathVariable Long clienteId) {
        return ResponseEntity.ok(service.listarEstadisticasProductosVendidosACliente(clienteId));
    }

    @GetMapping("/consultas/top5-mas-vendidos")
    public ResponseEntity<List<ProductoEstadisticaResponse>> top5ProductosMasVendidos() {
        return ResponseEntity.ok(service.listarTop5ProductosMasVendidos());
    }

    private void validarRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        if (inicio == null || fin == null) {
            throw new IllegalArgumentException(
                    "Los parámetros 'inicio' y 'fin' son obligatorios.");
        }
        if (fin.isBefore(inicio)) {
            throw new IllegalArgumentException(
                    "La fecha 'fin' no puede ser anterior a la fecha 'inicio'.");
        }
    }
}