package peru.edu.uls.ucos.operacionesrest.pago;

import java.time.LocalDateTime;

public record PagoResponse(
    Long id,
    String metodo,
    Double monto,
    LocalDateTime fechaPago,
    Long ventaId
) {}