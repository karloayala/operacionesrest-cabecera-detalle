package peru.edu.uls.ucos.operacionesrest.detallepedido;

public record DetallePedidoResponse(
    Long id,
    Long pedidoId,
    String numeroPedido,
    Long productoId,
    String productoNombre,
    Integer cantidad,
    Double precioUnitario,
    Double subtotal
)
{}
