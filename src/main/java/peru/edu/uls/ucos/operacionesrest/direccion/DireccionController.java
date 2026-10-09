package peru.edu.uls.ucos.operacionesrest.direccion;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
}