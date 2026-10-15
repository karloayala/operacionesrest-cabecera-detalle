package peru.edu.uls.ucos.operacionesrest.pedido;

import peru.edu.uls.ucos.operacionesrest.detallepedido.DetallePedidoRequest;

import java.util.List;

public record PedidoRequest(
    Long clienteId,
    List<DetallePedidoRequest> detalles
) {}