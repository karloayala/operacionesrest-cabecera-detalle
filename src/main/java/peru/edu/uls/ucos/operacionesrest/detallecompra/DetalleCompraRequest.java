package peru.edu.uls.ucos.operacionesrest.detallecompra;

public record DetalleCompraRequest(
    Integer cantidad,
    Double precioUnitario,
    Long compraId,
    Long productoId
) {}