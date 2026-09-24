package peru.edu.uls.ucos.operacionesrest.producto;

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
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ProductoResponse> registrar(@RequestBody ProductoRequest request) {
        ProductoResponse response = service.registrarProductoNuevo(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> consultarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.consultarProductoPorId(id));
    }

    @GetMapping("/marca/{marca}")
    public ResponseEntity<List<ProductoResponse>> consultarPorMarca(@PathVariable String marca) {
        return ResponseEntity.ok(service.consultarProductoPorMarca(marca));
    }
}