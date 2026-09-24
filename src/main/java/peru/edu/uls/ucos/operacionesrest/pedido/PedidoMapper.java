package peru.edu.uls.ucos.operacionesrest.pedido;

import org.springframework.stereotype.Component;

import peru.edu.uls.ucos.operacionesrest.cliente.Cliente;

@Component
public class PedidoMapper {

    public Pedido aEntidad(PedidoRequest request, Cliente cliente) {
        Double totalInicial = request.total() == null ? 0.0 : request.total();
        String estadoInicial = request.estado() == null || request.estado().isBlank()
                ? "PENDIENTE"
                : request.estado();
        return new Pedido(
            request.numeroPedido(),
            totalInicial,
            estadoInicial,
            cliente
        );
    }

    public PedidoResponse aRespuesta(Pedido pedido) {
        Cliente cliente = pedido.getCliente();
        return new PedidoResponse(
            pedido.getId(),
            pedido.getNumeroPedido(),
            pedido.getTotal(),
            pedido.getEstado(),
            cliente == null ? null : cliente.getId(),
            cliente == null ? null : cliente.getNombre(),
            cliente == null ? null : cliente.getDocumento()
        );
    }
}
