package peru.edu.uls.ucos.operacionesrest.proveedor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
public class ProveedorController {

    private final ProveedorService service;

    public ProveedorController(ProveedorService service) {
        this.service = service;
    }

    // 1. Registrar un nuevo proveedor en el directorio
    @PostMapping
    public ResponseEntity<ProveedorResponse> registrar(@RequestBody ProveedorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrarProveedor(request));
    }

    // 2. Consultar los datos de contacto de un proveedor específico
    @GetMapping("/{id}")
    public ResponseEntity<ProveedorResponse> consultarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.consultarPorId(id));
    }

    // 3. Obtener todo el directorio de proveedores de la organización
    @GetMapping
    public ResponseEntity<List<ProveedorResponse>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    // 4. Buscador de proveedores por nombre
    @GetMapping("/buscar")
    public ResponseEntity<List<ProveedorResponse>> buscarPorNombre(@RequestParam("nombre") String nombre) {
        return ResponseEntity.ok(service.buscarPorNombre(nombre));
    }
}