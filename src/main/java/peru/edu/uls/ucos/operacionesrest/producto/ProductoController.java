package peru.edu.uls.ucos.operacionesrest.producto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ProductoResponse> registrar(@RequestBody ProductoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrarProductoNuevo(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> consultarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.consultarProductoPorId(id));
    }
    
    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<ProductoResponse>> consultarPorCategoria(@PathVariable Long categoriaId) {
        return ResponseEntity.ok(service.consultarProductosPorCategoria(categoriaId));
    }
    
    // Consulta que utiliza el cruce de tablas Producto -> DetalleCompra -> Compra -> Proveedor
    @GetMapping("/proveedor/{proveedorId}")
    public ResponseEntity<List<ProductoResponse>> consultarPorProveedor(@PathVariable Long proveedorId) {
        return ResponseEntity.ok(service.consultarProductosPorProveedor(proveedorId));
    }

    // Consultar el Top 5 de productos más comprados para el inventario
    @GetMapping("/consultas/estadisticas/top5-abastecidos")
    public ResponseEntity<List<ProductoEstadisticaResponse>> top5ProductosMasAbastecidos() {
        return ResponseEntity.ok(service.listarTop5ProductosMasAbastecidos());
    }

    // Consultar cuánto dinero y volumen se ha invertido en productos de un proveedor
    @GetMapping("/consultas/estadisticas/proveedor/{proveedorId}")
    public ResponseEntity<List<ProductoEstadisticaResponse>> estadisticasPorProveedor(@PathVariable Long proveedorId) {
        return ResponseEntity.ok(service.listarEstadisticasPorProveedor(proveedorId));
    }

    @PutMapping("/stock/{id}")
    public ResponseEntity<Producto> actualizarStock(@PathVariable Long id, @RequestParam Integer nuevoStock) {
        Producto producto = service.actualizarStock(id, nuevoStock);
        return ResponseEntity.ok(producto);
    }
}