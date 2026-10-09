package peru.edu.uls.ucos.operacionesrest.direccion;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/direcciones")
public class DireccionController {

    private final DireccionService service;

    public DireccionController(DireccionService service) {
        this.service = service;
    }

    // GET: Obtener dirección por ID
    // curl -X GET http://localhost:8080/api/direcciones/1
    @GetMapping("/{id}")
    public ResponseEntity<DireccionResponse> consultarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.consultarPorId(id));
    }

    // POST: Registrar una nueva dirección
    // curl -X POST http://localhost:8080/api/direcciones -H "Content-Type: application/json" -d "{\"calle\":\"Av. Ejercito 123\",\"ciudad\":\"Arequipa\",\"codigoPostal\":\"04001\",\"clienteId\":1}"
    @PostMapping 
    public ResponseEntity<DireccionResponse> registrar(@RequestBody DireccionRequest request) {
        DireccionResponse response = service.registrarDireccion(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}