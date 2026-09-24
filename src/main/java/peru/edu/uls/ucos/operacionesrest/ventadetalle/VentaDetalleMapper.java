package peru.edu.uls.ucos.operacionesrest.ventadetalle;

import org.springframework.stereotype.Component;
import peru.edu.uls.ucos.operacionesrest.producto.Producto;
import peru.edu.uls.ucos.operacionesrest.venta.Venta;

@Component
public class VentaDetalleMapper {

    public VentaDetalle aEntidad(Venta venta, Producto producto, VentaDetalleRequest request) {
        Double precioUnitario = request.precioUnitario() != null ? request.precioUnitario() : producto.getPrecio();
        return new VentaDetalle(
            venta,
            producto,
            request.cantidad(),
            precioUnitario
        );
    }

    public VentaDetalleResponse aRespuesta(VentaDetalle detalle) {
        Venta venta = detalle.getVenta();
        Producto producto = detalle.getProducto();
        Double subtotal = detalle.getCantidad() * detalle.getPrecioUnitario();
        return new VentaDetalleResponse(
            detalle.getId(),
            venta == null ? null : venta.getId(),
            venta == null ? null : venta.getNumeroVenta(),
            producto == null ? null : producto.getId(),
            producto == null ? null : producto.getNombre(),
            detalle.getCantidad(),
            detalle.getPrecioUnitario(),
            subtotal
        );
    }
}
