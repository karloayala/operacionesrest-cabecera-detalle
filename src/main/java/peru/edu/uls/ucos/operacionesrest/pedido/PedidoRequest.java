package peru.edu.uls.ucos.operacionesrest.pedido;

public record PedidoRequest(
    String numeroPedido,
    Double total,
    String estado,
    Long clienteId
)
{}
