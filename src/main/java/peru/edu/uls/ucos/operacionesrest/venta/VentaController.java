package peru.edu.uls.ucos.operacionesrest.venta;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    // 1. GET: Obtener una venta por su ID
    // curl -X GET http://localhost:8080/api/ventas/1
    @GetMapping("/{id}")
    public ResponseEntity<VentaResponse> consultarVentaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ventaService.consultarVentaPorId(id));
    }

    // 2. GET: Obtener las ventas por su estado
    // curl -X GET http://localhost:8080/api/ventas/estado/CONFIRMADA
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<VentaResponse>> consultarPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(ventaService.consultarPorEstado(estado));
    }

    // 3. GET: Obtener las ventas de un cliente por su ID
    // curl -X GET http://localhost:8080/api/ventas/cliente/1
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<VentaResponse>> consultarPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(ventaService.consultarPorCliente(clienteId));
    }

    // 4. POST: Confirmar un pedido y generar su venta
    // curl -X POST http://localhost:8080/api/ventas/confirmar-pedido/4
    @PostMapping("/confirmar-pedido/{pedidoId}")
    public ResponseEntity<VentaResponse> confirmarPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ventaService.confirmarPedido(pedidoId));
    }

    // 5. GET: Obtener las ventas que contienen un producto específico en sus detalles
    // curl -X GET http://localhost:8080/api/ventas/producto/2
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<VentaResponse>> consultarPorProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(ventaService.consultarPorProducto(productoId));
    }
}
