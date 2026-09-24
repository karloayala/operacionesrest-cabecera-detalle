package peru.edu.uls.ucos.operacionesrest.detallepedido;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/detalles")
public class DetallePedidoController {

    private final DetallePedidoService service;

    public DetallePedidoController(DetallePedidoService service) {
        this.service = service;
    }

    // 1. POST /api/detalles
    @PostMapping
    public ResponseEntity<DetallePedidoResponse> agregarDetalle(@RequestBody DetallePedidoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.agregarDetalle(request));
    }

    // 2. GET /api/detalles/{id}
    @GetMapping("/{id}")
    public ResponseEntity<DetallePedidoResponse> consultarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.consultarPorId(id));
    }

    // 3. GET /api/detalles/pedido/{pedidoId}
    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<List<DetallePedidoResponse>> listarPorPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(service.listarPorPedido(pedidoId));
    }
}
