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
    
    // 1. GET: Obtener detalle de pedido por su ID
    // curl -X GET http://localhost:8080/api/detalles/1
    @GetMapping("/{id}")
    public ResponseEntity<DetallePedidoResponse> consultarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.consultarPorId(id));
    }

    // 2. GET: Listar detalles de un pedido por su ID
    // curl -X GET http://localhost:8080/api/detalles/pedido/1
    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<List<DetallePedidoResponse>> listarPorPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(service.listarPorPedido(pedidoId));
    }

    // 3. POST: Agregar un detalle a un pedido por su ID
    // curl -X POST http://localhost:8080/api/detalles/agregar/1 \
    //      -H "Content-Type: application/json" \
    //      -d '{"productoId":1,"cantidad":2,"precioUnitario":25.50}'    
    @PostMapping("/agregar/{id}")
    public ResponseEntity<DetallePedidoResponse> agregarDetalle(@PathVariable Long id, @RequestBody DetallePedidoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.agregarDetalle(id,request));
    }
}
