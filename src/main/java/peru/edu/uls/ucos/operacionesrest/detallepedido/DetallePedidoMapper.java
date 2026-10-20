package peru.edu.uls.ucos.operacionesrest.detallepedido;

import org.springframework.stereotype.Component;

import peru.edu.uls.ucos.operacionesrest.pedido.Pedido;
import peru.edu.uls.ucos.operacionesrest.producto.Producto;

@Component
public class DetallePedidoMapper {

    public DetallePedido aEntidad(Pedido pedido, Producto producto, Integer cantidad, Double precioUnitario) {
        return new DetallePedido(
            pedido,
            producto,
            cantidad,
            precioUnitario
        );
    }

    public DetallePedidoResponse aRespuesta(DetallePedido detalle) {
        Pedido pedido = detalle.getPedido();
        Producto producto = detalle.getProducto();
        Double subtotal = detalle.getCantidad() * detalle.getPrecioUnitario();
        return new DetallePedidoResponse(
            detalle.getId(),
            pedido == null ? null : pedido.getId(),
            pedido == null ? null : pedido.getFecha(),
            producto == null ? null : producto.getId(),
            producto == null ? null : producto.getNombre(),
            detalle.getCantidad(),
            detalle.getPrecioUnitario(),
            subtotal
        );
    }
}
