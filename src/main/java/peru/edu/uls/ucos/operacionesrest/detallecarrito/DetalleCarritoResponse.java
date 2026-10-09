package peru.edu.uls.ucos.operacionesrest.detallecarrito;

public record DetalleCarritoResponse(
    Long id,
    Long carritoId,
    Long productoId,
    String nombreProducto,
    Integer cantidad,
    Double precioUnitario,
    Double subtotal
) {}
