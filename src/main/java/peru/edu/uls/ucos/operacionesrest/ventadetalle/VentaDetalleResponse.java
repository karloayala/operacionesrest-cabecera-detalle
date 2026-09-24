package peru.edu.uls.ucos.operacionesrest.ventadetalle;

public record VentaDetalleResponse(
    Long id,
    Long ventaId,
    String numeroVenta,
    Long productoId,
    String productoNombre,
    Integer cantidad,
    Double precioUnitario,
    Double subtotal
) {}
