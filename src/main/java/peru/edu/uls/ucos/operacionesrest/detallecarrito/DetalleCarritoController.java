package peru.edu.uls.ucos.operacionesrest.detallecarrito;

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
@RequestMapping("/api/detalle-carrito")
public class DetalleCarritoController {

    private final DetalleCarritoService service;

    public DetalleCarritoController(DetalleCarritoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<DetalleCarritoResponse> agregarDetalle(@RequestBody DetalleCarritoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.agregarDetalle(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetalleCarritoResponse> consultarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.consultarPorId(id));
    }

    @GetMapping("/carrito/{carritoId}")
    public ResponseEntity<List<DetalleCarritoResponse>> listarPorCarrito(@PathVariable Long carritoId) {
        return ResponseEntity.ok(service.listarPorCarrito(carritoId));
    }
}
