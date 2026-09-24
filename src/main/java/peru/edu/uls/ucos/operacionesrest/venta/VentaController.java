package peru.edu.uls.ucos.operacionesrest.venta;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentaResponse> consultarVentaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ventaService.consultarVentaPorId(id));
    }

    @PostMapping
    public ResponseEntity<VentaResponse> registrarVenta(@RequestBody VentaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ventaService.registrarVenta(request));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<VentaResponse>> consultarPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(ventaService.consultarPorEstado(estado));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<VentaResponse>> consultarPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(ventaService.consultarPorCliente(clienteId));
    }
}
