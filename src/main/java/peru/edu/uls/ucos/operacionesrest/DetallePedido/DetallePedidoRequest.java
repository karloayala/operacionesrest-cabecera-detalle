package peru.edu.uls.ucos.operacionesrest.detallepedido;

public record DetallePedidoRequest(
    Long pedidoId,
    Long productoId,
    Integer cantidad,
    Double precioUnitario
)
{}
