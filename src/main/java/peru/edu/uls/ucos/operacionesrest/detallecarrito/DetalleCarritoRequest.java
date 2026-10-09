package peru.edu.uls.ucos.operacionesrest.detallecarrito;

public record DetalleCarritoRequest(
    Long carritoId,
    Long productoId,
    Integer cantidad,
    Double precioUnitario
) {}
