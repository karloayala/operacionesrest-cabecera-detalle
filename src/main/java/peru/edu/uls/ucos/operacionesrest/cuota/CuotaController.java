package peru.edu.uls.ucos.operacionesrest.cuota;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cuotas")
public class CuotaController {

    private final CuotaService service;

    public CuotaController(CuotaService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CuotaResponse> consultarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.consultarPorId(id));
    }

    @PostMapping
    public ResponseEntity<CuotaResponse> registrarCuota(@RequestBody CuotaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrarCuota(request));
    }
}
