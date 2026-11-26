package peru.edu.uls.ucos.operacionesrest.producto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    boolean existsByNombreIgnoreCase(String nombre);
    
    List<Producto> findByMarcaIgnoreCase(String marca);
    
    List<Producto> findByCategoriaId(Long categoriaId);

    // Estadística: Top 5 productos en los que más hemos invertido/comprado a proveedores
    @Query(value = "SELECT p.id AS productoId, p.nombre AS productoNombre, p.marca AS productoMarca, SUM(dc.cantidad) AS cantidadTotal, SUM(dc.cantidad * dc.precio_unitario) AS montoTotal FROM productos p INNER JOIN detalle_compras dc ON dc.producto_id = p.id GROUP BY p.id, p.nombre, p.marca ORDER BY cantidadTotal DESC LIMIT 5", nativeQuery = true)
    List<ProductoEstadisticaProjection> findTop5ProductosMasAbastecidos();

    // Estadística: Resumen de productos comprados a un proveedor específico
    @Query(value = "SELECT p.id AS productoId, p.nombre AS productoNombre, p.marca AS productoMarca, SUM(dc.cantidad) AS cantidadTotal, SUM(dc.cantidad * dc.precio_unitario) AS montoTotal FROM productos p INNER JOIN detalle_compras dc ON dc.producto_id = p.id INNER JOIN compras c ON dc.compra_id = c.id WHERE c.proveedor_id = :proveedorId GROUP BY p.id, p.nombre, p.marca ORDER BY cantidadTotal DESC", nativeQuery = true)
    List<ProductoEstadisticaProjection> findEstadisticasAbastecimientoPorProveedor(@Param("proveedorId") Long proveedorId);
    
    // Consulta JPQL: Obtener productos que alguna vez fueron abastecidos por un proveedor específico
    @Query("SELECT DISTINCT p FROM Producto p JOIN p.detallesCompra dc JOIN dc.compra c WHERE c.proveedor.id = :proveedorId")
    List<Producto> findProductosAbastecidosPorProveedor(@Param("proveedorId") Long proveedorId);

    // Obtener todos los productos que nos ha vendido un proveedor específico
    @Query("SELECT DISTINCT p FROM Producto p JOIN DetalleCompra dc ON p.id = dc.producto.id JOIN Compra c ON dc.compra.id = c.id WHERE c.proveedor.id = :proveedorId")
    List<Producto> findProductosByProveedorId(@Param("proveedorId") Long proveedorId);
}