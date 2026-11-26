package peru.edu.uls.ucos.operacionesrest.planpago;

import java.time.LocalDateTime;
import java.util.List;

public record PlanPagoRequest(
        Integer cantCuotas,
        Long ventaId,
        List<CuotaPlanPagoRequest> cuotas
) {
    public PlanPagoRequest(Integer cantCuotas, Long ventaId) {
        this(cantCuotas, ventaId, null);
    }

    public record CuotaPlanPagoRequest(
            Integer numeroCuota,
            Double monto,
            LocalDateTime fechaVencimiento,
            String estado
    ) {}
}
