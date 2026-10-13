package peru.edu.uls.ucos.operacionesrest.detallecompra;

public record DetalleCompraResponse(
    Long id,
    Integer cantidad,
    Double precioUnitario,
    Long compraId,
    Long productoId
) {}