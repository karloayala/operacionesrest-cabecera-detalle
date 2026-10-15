package peru.edu.uls.ucos.operacionesrest.cuota;

import java.time.LocalDateTime;

public record CuotaRequest(
        Integer numeroCuota,
        Double monto,
        LocalDateTime fechaVencimiento,
        String estado,
        Long planPagoId
) {}
