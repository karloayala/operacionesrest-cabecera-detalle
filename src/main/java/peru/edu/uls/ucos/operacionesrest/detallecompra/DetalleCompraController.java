package peru.edu.uls.ucos.operacionesrest.detallecompra;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/detalle-compras")
public class DetalleCompraController {

    private final DetalleCompraService service;

    public DetalleCompraController(DetalleCompraService service) {
        this.service = service;
    }

    // 1. Registrar un producto dentro de una compra (Aumenta el stock automáticamente)
    @PostMapping
    public ResponseEntity<DetalleCompraResponse> registrar(@RequestBody DetalleCompraRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrarDetalle(request));
    }

    // 2. Consultar un detalle de compra específico por su ID
    @GetMapping("/{id}")
    public ResponseEntity<DetalleCompraResponse> consultarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.consultarPorId(id));
    }

    // 3. Ver todos los productos asociados a una compra/factura de proveedor específica
    @GetMapping("/compra/{compraId}")
    public ResponseEntity<List<DetalleCompraResponse>> consultarPorCompra(@PathVariable Long compraId) {
        return ResponseEntity.ok(service.consultarPorCompra(compraId));
    }

    // 4. Ver el historial de compras de abastecimiento para un producto específico
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<DetalleCompraResponse>> consultarPorProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(service.consultarPorProducto(productoId));
    }
}