package peru.edu.uls.ucos.operacionesrest.producto;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    // POST http://localhost:8080/api/productos
    @PostMapping
    public ResponseEntity<ProductoResponse> registrar(@RequestBody ProductoRequest request) {
        ProductoResponse response = service.registrarProductoNuevo(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET http://localhost:8080/api/productos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> consultarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.consultarProductoPorId(id));
    }

    // GET http://localhost:8080/api/productos/marca/{marca}
    @GetMapping("/marca/{marca}")
    public ResponseEntity<List<ProductoResponse>> consultarPorMarca(@PathVariable String marca) {
        return ResponseEntity.ok(service.consultarProductoPorMarca(marca));
    }

    // GET http://localhost:8080/api/productos/consultas/en-pedidos
    @GetMapping("/consultas/en-pedidos")
    public ResponseEntity<List<ProductoResponse>> productosIncluidosEnPedidos() {
        return ResponseEntity.ok(service.listarProductosIncluidosEnPedidos());
    }

    // GET http://localhost:8080/api/productos/consultas/vendidos
    @GetMapping("/consultas/vendidos")
    public ResponseEntity<List<ProductoResponse>> productosVendidos() {
        return ResponseEntity.ok(service.listarProductosVendidos());
    }

    // GET http://localhost:8080/api/productos/consultas/vendidos-stock-bajo?stockMaximo={stockMaximo}
    @GetMapping("/consultas/vendidos-stock-bajo")
    public ResponseEntity<List<ProductoResponse>> productosVendidosConStockBajo(
            @RequestParam("stockMaximo") Integer stockMaximo) {
        return ResponseEntity.ok(service.listarProductosVendidosConStockBajo(stockMaximo));
    }

    // GET http://localhost:8080/api/productos/consultas/pedidos/cliente/{clienteId}
    @GetMapping("/consultas/pedidos/cliente/{clienteId}")
    public ResponseEntity<List<ProductoResponse>> productosPedidosPorCliente(
            @PathVariable Long clienteId) {
        return ResponseEntity.ok(service.listarProductosPedidosPorCliente(clienteId));
    }

    // GET http://localhost:8080/api/productos/consultas/vendidos-entre?inicio={inicio}&fin={fin}
    @GetMapping("/consultas/vendidos-entre")
    public ResponseEntity<List<ProductoResponse>> productosVendidosEntreFechas(
            @RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam("fin")    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        validarRangoFechas(inicio, fin);
        return ResponseEntity.ok(service.listarProductosVendidosEntreFechas(inicio, fin));
    }

    // GET http://localhost:8080/api/productos/consultas/pedidos-y-ventas
    @GetMapping("/consultas/pedidos-y-ventas")
    public ResponseEntity<List<ProductoResponse>> productosPedidosYVendidos() {
        return ResponseEntity.ok(service.listarProductosPedidosYVendidos());
    }

    // GET http://localhost:8080/api/productos/consultas/estadisticas/cliente/{clienteId}
    @GetMapping("/consultas/estadisticas/cliente/{clienteId}")
    public ResponseEntity<List<ProductoEstadisticaResponse>> estadisticasProductosVendidosACliente(
            @PathVariable Long clienteId) {
        return ResponseEntity.ok(service.listarEstadisticasProductosVendidosACliente(clienteId));
    }

    // GET http://localhost:8080/api/productos/consultas/top5-mas-vendidos
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