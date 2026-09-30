package peru.edu.uls.ucos.operacionesrest.ventadetalle;

import java.time.LocalDateTime;

public record VentaDetalleResponse(
    Long id,
    Long ventaId,
    LocalDateTime fecha,
    Long productoId,
    String productoNombre,
    Integer cantidad,
    Double precioUnitario,
    Double subtotal
) {}
