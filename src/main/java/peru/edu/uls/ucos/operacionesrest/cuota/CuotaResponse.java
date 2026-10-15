package peru.edu.uls.ucos.operacionesrest.cuota;

import java.time.LocalDateTime;

public record CuotaResponse(
        Long id,
        Integer numeroCuota,
        Double monto,
        LocalDateTime fechaVencimiento,
        String estado,
        Long planPagoId,
        Long pagoId
) {}
