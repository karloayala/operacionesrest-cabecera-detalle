package peru.edu.uls.ucos.operacionesrest.pedido;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum EstadoPedido {
    PENDIENTE,
    CONFIRMADO,
    CANCELADO,
    ENTREGADO
}