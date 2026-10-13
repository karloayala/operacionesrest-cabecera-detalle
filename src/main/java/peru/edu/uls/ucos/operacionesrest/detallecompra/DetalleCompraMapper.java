package peru.edu.uls.ucos.operacionesrest.detallecompra;

import org.springframework.stereotype.Component;
import peru.edu.uls.ucos.operacionesrest.compra.Compra;
import peru.edu.uls.ucos.operacionesrest.producto.Producto;

@Component
public class DetalleCompraMapper {
    
    public DetalleCompra aEntidad(DetalleCompraRequest request, Compra compra, Producto producto) {
        return new DetalleCompra(compra, producto, request.cantidad(), request.precioUnitario());
    }

    public DetalleCompraResponse aRespuesta(DetalleCompra detalle) {
        Long compraId = detalle.getCompra() != null ? detalle.getCompra().getId() : null;
        Long productoId = detalle.getProducto() != null ? detalle.getProducto().getId() : null;
        
        return new DetalleCompraResponse(
            detalle.getId(),
            detalle.getCantidad(),
            detalle.getPrecioUnitario(),
            compraId,
            productoId
        );
    }
}