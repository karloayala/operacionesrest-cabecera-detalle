package peru.edu.uls.ucos.operacionesrest.ventadetalle;

public record VentaDetalleRequest(
    Long productoId,
    Integer cantidad,
    Double precioUnitario
) {}
