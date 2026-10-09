package peru.edu.uls.ucos.operacionesrest.carrito;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/carritos")
public class CarritoController {

    private final CarritoService service;

    public CarritoController(CarritoService service) {
        this.service = service;
    }

    // GET: Obtener carrito por ID
    // curl -X GET http://localhost:8080/api/carritos/1
    @GetMapping("/{id}")
    public ResponseEntity<CarritoResponse> consultarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.consultarPorId(id));
    }

    // POST: Registrar un nuevo carrito
    // curl -X POST http://localhost:8080/api/carritos -H "Content-Type: application/json" -d "{\"estado\":\"ACTIVO\",\"clienteId\":1}"
    @PostMapping 
    public ResponseEntity<CarritoResponse> registrar(@RequestBody CarritoRequest request) {
        CarritoResponse response = service.registrarCarrito(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}