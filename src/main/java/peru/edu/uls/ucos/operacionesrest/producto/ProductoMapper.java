package peru.edu.uls.ucos.operacionesrest.producto;

import org.springframework.stereotype.Component;

@Component
public class ProductoMapper {
    public Producto aEntidad(ProductoRequest request) {
        return new Producto(request.nombre(), request.marca(), request.precio(), request.stock());
    }

    public ProductoResponse aRespuesta(Producto producto) {
        return new ProductoResponse(producto.getId(), producto.getNombre(), producto.getMarca(), producto.getPrecio(), producto.getStock());
    }

    public ProductoEstadisticaResponse aRespuestaEstadistica(ProductoEstadisticaProjection projection) {
        return new ProductoEstadisticaResponse(
                projection.getProductoId(),
                projection.getProductoNombre(),
                projection.getProductoMarca(),
                projection.getCantidadTotal(),
                projection.getMontoTotal()
        );
    }
}