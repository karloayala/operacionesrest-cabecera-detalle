package peru.edu.uls.ucos.operacionesrest.cliente;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }
    // GET: Obtener cliente por su ID
    // curl -X GET http://localhost:8080/api/clientes/1
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> consultarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.consultarClientePorId(id));
    }

    // GET: Obtener cliente por su número de documento
    // curl -X GET http://localhost:8080/api/clientes/documento/12345678
    @GetMapping("/documento/{documento}")
    public ResponseEntity<ClienteResponse> consultarPorDocumento(@PathVariable String documento) {
        return ResponseEntity.ok(service.consultarClientePorDocumento(documento));
    }

    // GET: Obtener clientes con ventas mayores a un monto dado
    // curl -X GET http://localhost:8080/api/clientes/ventas-mayores/100.0
    @GetMapping("/ventas-mayores/{monto}")
    public ResponseEntity<?> obtenerClientesConVentasMayoresA(@PathVariable Double monto) {
        return ResponseEntity.ok(service.obtenerClientesConVentasMayoresA(monto));
    }

    // GET: Obtener clientes que compraron un producto específico
    // curl -X GET http://localhost:8080/api/clientes/compraron-producto/3
    @GetMapping("/compraron-producto/{idProducto}")
    public ResponseEntity<?> obtenerClientesQueCompraronProducto(@PathVariable Long idProducto) {
        return ResponseEntity.ok(service.obtenerClientesQueCompraronProducto(idProducto));
    }
    // POST: Registrar un nuevo cliente
    // curl -X POST http://localhost:8080/api/clientes -H "Content-Type: application/json" -d "{\"nombre\":\"Juan Perez\",\"documento\":\"12345678\"}"
    @PostMapping
    public ResponseEntity<ClienteResponse> registrar(@RequestBody ClienteRequest request) {
        ClienteResponse response = service.registrarClienteNuevo(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}