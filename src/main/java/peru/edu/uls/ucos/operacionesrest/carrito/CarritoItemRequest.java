package peru.edu.uls.ucos.operacionesrest.carrito;

public record CarritoItemRequest(
    Long productoId,
    Integer cantidad,
    Double precioUnitario
) {}
