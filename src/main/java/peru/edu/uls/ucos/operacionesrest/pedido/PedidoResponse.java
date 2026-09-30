package peru.edu.uls.ucos.operacionesrest.pedido;

import peru.edu.uls.ucos.operacionesrest.detallepedido.DetallePedidoResponse;

import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponse(
    Long id,
    LocalDateTime fecha,
    Double total,
    String estado,
    Long clienteId,
    String clienteNombre,
    String clienteDocumento,
    List<DetallePedidoResponse> detalles
) {}