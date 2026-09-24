package peru.edu.uls.ucos.operacionesrest.detallepedido;

public record DetallePedidoRequest(
    Long productoId,
    Integer cantidad
) {}