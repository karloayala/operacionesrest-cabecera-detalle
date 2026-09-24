package peru.edu.uls.ucos.operacionesrest.pedido;

public record PedidoResponse(
    Long id,
    String numeroPedido,
    Double total,
    String estado,
    Long clienteId,
    String clienteNombre,
    String clienteDocumento
)
{}
