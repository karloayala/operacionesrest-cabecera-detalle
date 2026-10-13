package peru.edu.uls.ucos.operacionesrest.producto;

import org.springframework.stereotype.Component;
import peru.edu.uls.ucos.operacionesrest.categoria.Categoria;

@Component
public class ProductoMapper {
    
    public Producto aEntidad(ProductoRequest request, Categoria categoria) {
        Producto producto = new Producto(request.nombre(), request.marca(), request.precio(), request.stock());
        producto.setCategoria(categoria);
        return producto;
    }

    public ProductoResponse aRespuesta(Producto producto) {
        Long categoriaId = producto.getCategoria() != null ? producto.getCategoria().getId() : null;
        
        return new ProductoResponse(
            producto.getId(), 
            producto.getNombre(), 
            producto.getMarca(), 
            producto.getPrecio(), 
            producto.getStock(),
            categoriaId
        );
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