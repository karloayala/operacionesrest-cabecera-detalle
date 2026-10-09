package peru.edu.uls.ucos.operacionesrest.producto;

import org.springframework.stereotype.Component;
import peru.edu.uls.ucos.operacionesrest.categoria.Categoria;
import peru.edu.uls.ucos.operacionesrest.proveedor.Proveedor;

@Component
public class ProductoMapper {
    
    public Producto aEntidad(ProductoRequest request, Categoria categoria, Proveedor proveedor) {
        Producto producto = new Producto(request.nombre(), request.marca(), request.precio(), request.stock());
        producto.setCategoria(categoria);
        producto.setProveedor(proveedor);
        return producto;
    }

    public ProductoResponse aRespuesta(Producto producto) {
        Long categoriaId = producto.getCategoria() != null ? producto.getCategoria().getId() : null;
        Long proveedorId = producto.getProveedor() != null ? producto.getProveedor().getId() : null;
        
        return new ProductoResponse(
            producto.getId(), 
            producto.getNombre(), 
            producto.getMarca(), 
            producto.getPrecio(), 
            producto.getStock(),
            categoriaId,
            proveedorId
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