package peru.edu.uls.ucos.operacionesrest.detallecarrito;

import org.springframework.stereotype.Component;

import peru.edu.uls.ucos.operacionesrest.carrito.Carrito;
import peru.edu.uls.ucos.operacionesrest.producto.Producto;

@Component
public class DetalleCarritoMapper {

    public DetalleCarrito aEntidad(Carrito carrito, Producto producto, DetalleCarritoRequest request) {
        return new DetalleCarrito(
            carrito,
            producto,
            request.cantidad(),
            request.precioUnitario()
        );
    }

    public DetalleCarritoResponse aRespuesta(DetalleCarrito detalle) {
        Carrito carrito = detalle.getCarrito();
        Producto producto = detalle.getProducto();
        Double subtotal = detalle.getCantidad() * detalle.getPrecioUnitario();

        return new DetalleCarritoResponse(
            detalle.getId(),
            carrito == null ? null : carrito.getId(),
            producto == null ? null : producto.getId(),
            producto == null ? null : producto.getNombre(),
            detalle.getCantidad(),
            detalle.getPrecioUnitario(),
            subtotal
        );
    }
}
