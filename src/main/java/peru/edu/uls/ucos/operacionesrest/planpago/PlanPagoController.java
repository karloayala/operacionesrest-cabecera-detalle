package peru.edu.uls.ucos.operacionesrest.planpago;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/planes-pago")
public class PlanPagoController {

    private final PlanPagoService service;

    public PlanPagoController(PlanPagoService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanPagoResponse> consultarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.consultarPorId(id));
    }

    @PostMapping
    public ResponseEntity<PlanPagoResponse> registrarPlanPago(@RequestBody PlanPagoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrarPlanPago(request));
    }
}
