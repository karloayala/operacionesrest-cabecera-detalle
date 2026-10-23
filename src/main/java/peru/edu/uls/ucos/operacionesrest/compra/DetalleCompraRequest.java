package peru.edu.uls.ucos.operacionesrest.compra;

public record DetalleCompraRequest(
    Long productoId,
    Integer cantidad,
    Double precioUnitario
) {}