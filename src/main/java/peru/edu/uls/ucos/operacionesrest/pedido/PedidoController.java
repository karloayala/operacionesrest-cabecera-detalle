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

    // 1. GET: Obtener todos los pedidos con sus detalles y productos
    // curl -X GET http://localhost:8080/api/pedidos
    @GetMapping
    public ResponseEntity<List<PedidoResponse>> consultarTodos() {
        return ResponseEntity.ok(pedidoService.consultarTodosConDetallesYProductos());
    }

    // 2. GET: Obtener un pedido por su ID
    // curl -X GET http://localhost:8080/api/pedidos/1
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> consultarPedidoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.consultarPedidoPorId(id));
    }

    // 3. GET: Obtener pedidos por el documento del cliente
    // curl -X GET http://localhost:8080/api/pedidos/documento/12345678
    @GetMapping("/documento/{documento}")
    public ResponseEntity<List<PedidoResponse>> consultarPorDocumentoCliente(@PathVariable String documento) {
        return ResponseEntity.ok(pedidoService.consultarPorDocumentoCliente(documento));
    }

    // 4. GET: Obtener pedidos por su estado
    // curl -X GET http://localhost:8080/api/pedidos/estado/PENDIENTE
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<PedidoResponse>> consultarPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(pedidoService.consultarPorEstado(estado));
    }

    // 5. GET: Obtener pedidos de un cliente por su ID
    // curl -X GET http://localhost:8080/api/pedidos/cliente/1
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<PedidoResponse>> consultarPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(pedidoService.consultarPorCliente(clienteId));
    }

    // 6. POST: Crear un nuevo pedido
    // curl -X POST http://localhost:8080/api/pedidos \
    //      -H "Content-Type: application/json" \
    //      -d '{"clienteId":1, "detalles":[{"productoId":2, "cantidad": 1}]}'
    @PostMapping
    public ResponseEntity<PedidoResponse> crearPedido(@RequestBody PedidoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.crearPedido(request));
    }

}
