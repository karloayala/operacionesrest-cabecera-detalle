package peru.edu.uls.ucos.operacionesrest.cliente;

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
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ClienteResponse> registrar(@RequestBody ClienteRequest request) {
        ClienteResponse response = service.registrarClienteNuevo(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> consultarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.consultarClientePorId(id));
    }

    @GetMapping("/documento/{documento}")
    public ResponseEntity<ClienteResponse> consultarPorDocumento(@PathVariable String documento) {
        return ResponseEntity.ok(service.consultarClientePorDocumento(documento));
    }

    // Probar consulta nativa 1:
    @GetMapping("/ventas-mayores/{monto}")
    public ResponseEntity<?> obtenerClientesConVentasMayoresA(@PathVariable Double monto) {
        return ResponseEntity.ok(service.obtenerClientesConVentasMayoresA(monto));
    }

    // Probar consulta nativa 2: 
    @GetMapping("/compraron-producto/{idProducto}")
    public ResponseEntity<?> obtenerClientesQueCompraronProducto(@PathVariable Long idProducto) {
        return ResponseEntity.ok(service.obtenerClientesQueCompraronProducto(idProducto));
    }

}