package peru.edu.uls.ucos.operacionesrest.ventadetalle;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ventas-detalles")
public class VentaDetalleController {

    private final VentaDetalleService service;

    public VentaDetalleController(VentaDetalleService service) {
        this.service = service;
    }

    // 1. GET: Obtener el detalle de una venta por su ID
    // curl -X GET http://localhost:8080/api/ventas-detalles/1
    @GetMapping("/{id}")
    public ResponseEntity<VentaDetalleResponse> consultarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.consultarPorId(id));
    }
}