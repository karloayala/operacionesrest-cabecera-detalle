package peru.edu.uls.ucos.operacionesrest.pedido;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    // 1. GET /api/pedidos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> consultarPedidoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.consultarPedidoPorId(id));
    }

    // 2. POST /api/pedidos
    @PostMapping
    public ResponseEntity<PedidoResponse> registrarProductoNuevo(@RequestBody PedidoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.registrarProductoNuevo(request));
    }

    // 3. GET /api/pedidos/estado/{estado}
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<PedidoResponse>> consultarPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(pedidoService.consultarPorEstado(estado));
    }

    // 4. GET /api/pedidos/cliente/{clienteId}
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<PedidoResponse>> consultarPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(pedidoService.consultarPorCliente(clienteId));
    }
}
