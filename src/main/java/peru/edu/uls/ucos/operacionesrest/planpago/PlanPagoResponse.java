package peru.edu.uls.ucos.operacionesrest.planpago;

import peru.edu.uls.ucos.operacionesrest.cuota.CuotaResponse;

import java.util.List;

public record PlanPagoResponse(
        Long id,
        String tipoPago,
        Integer cantCuotas,
        Long ventaId,
        List<CuotaResponse> cuotas
) {}
