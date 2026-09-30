package peru.edu.uls.ucos.operacionesrest.detallepedido;

import java.time.LocalDateTime;

public record DetallePedidoResponse(
    Long id,
    Long pedidoId,
    LocalDateTime fecha,
    Long productoId,
    String productoNombre,
    Integer cantidad,
    Double precioUnitario,
    Double subtotal
)
{}
