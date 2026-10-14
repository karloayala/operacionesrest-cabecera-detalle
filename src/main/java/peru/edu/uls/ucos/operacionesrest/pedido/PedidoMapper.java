package peru.edu.uls.ucos.operacionesrest.pedido;

import org.springframework.stereotype.Component;

import peru.edu.uls.ucos.operacionesrest.cliente.Cliente;
import peru.edu.uls.ucos.operacionesrest.detallepedido.DetallePedidoMapper;
import peru.edu.uls.ucos.operacionesrest.detallepedido.DetallePedidoResponse;

import java.util.List;

@Component
public class PedidoMapper {

    private final DetallePedidoMapper detalleMapper;

    public PedidoMapper(DetallePedidoMapper detalleMapper) {
        this.detalleMapper = detalleMapper;
    }

    public Pedido aEntidad(PedidoRequest request, Cliente cliente) {
        return new Pedido(0.0,cliente);
    }

    public PedidoResponse aRespuesta(Pedido pedido) {
        Cliente cliente = pedido.getCliente();
        List<DetallePedidoResponse> detallesResp = pedido.getDetalles() == null 
                ? List.of() 
                : pedido.getDetalles().stream().map(detalleMapper::aRespuesta).toList();
        Long ventaId = pedido.getVenta() != null ? pedido.getVenta().getId() : null;

        return new PedidoResponse(
            pedido.getId(),
            pedido.getFecha(),
            pedido.getTotal(),
            pedido.getEstado(),
            cliente == null ? null : cliente.getId(),
            cliente == null ? null : cliente.getNombre(),
            cliente == null ? null : cliente.getDocumento(),
            detallesResp,
            ventaId
        );
    }
}