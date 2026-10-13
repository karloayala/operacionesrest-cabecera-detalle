package peru.edu.uls.ucos.operacionesrest.compra;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/compras")
public class CompraController {

    private final CompraService service;

    public CompraController(CompraService service) {
        this.service = service;
    }

    // 1. Registrar una nueva compra (Abastecimiento)
    @PostMapping
    public ResponseEntity<CompraResponse> registrar(@RequestBody CompraRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrarCompra(request));
    }

    // 2. Consultar compra específica por ID
    @GetMapping("/{id}")
    public ResponseEntity<CompraResponse> consultarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.consultarPorId(id));
    }

    // 3. Consultar todo el historial de compras hechas a un proveedor específico
    @GetMapping("/proveedor/{proveedorId}")
    public ResponseEntity<List<CompraResponse>> consultarPorProveedor(@PathVariable Long proveedorId) {
        return ResponseEntity.ok(service.consultarPorProveedor(proveedorId));
    }

    // 4. Filtrar las compras por su estado (ej. "RECIBIDO", "PENDIENTE")
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<CompraResponse>> consultarPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(service.consultarPorEstado(estado));
    }

    // 5. Consultar compras en un rango de fechas para reportes y auditoría contable
    @GetMapping("/fechas")
    public ResponseEntity<List<CompraResponse>> consultarPorFechas(
            @RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam("fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(service.consultarPorRangoDeFechas(inicio, fin));
    }
}