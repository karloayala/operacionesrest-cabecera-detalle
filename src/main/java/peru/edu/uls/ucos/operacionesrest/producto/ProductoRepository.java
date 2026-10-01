package peru.edu.uls.ucos.operacionesrest.producto;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    boolean existsByNombreIgnoreCase(String nombre);
    
    List<Producto> findByMarcaIgnoreCase(String marca);

    @Query("SELECT DISTINCT p FROM Producto p JOIN DetallePedido dp ON dp.producto.id = p.id")
    List<Producto> findProductosIncluidosEnPedidos();

    @Query("SELECT DISTINCT p FROM Producto p JOIN VentaDetalle vd ON vd.producto.id = p.id")
    List<Producto> findProductosVendidos();

    @Query("SELECT DISTINCT p FROM Producto p JOIN VentaDetalle vd ON vd.producto.id = p.id WHERE p.stock <= :stockMaximo")
    List<Producto> findProductosVendidosConStockBajo(@Param("stockMaximo") Integer stockMaximo);

    @Query("SELECT DISTINCT p FROM Producto p JOIN DetallePedido dp ON dp.producto.id = p.id JOIN Pedido pe ON dp.pedido.id = pe.id JOIN Cliente c ON pe.cliente.id = c.id WHERE c.id = :clienteId")
    List<Producto> findProductosPedidosPorCliente(@Param("clienteId") Long clienteId);

    @Query("SELECT DISTINCT p FROM Producto p JOIN VentaDetalle vd ON vd.producto.id = p.id JOIN Venta v ON vd.venta.id = v.id WHERE v.fecha BETWEEN :fechaInicio AND :fechaFin")
    List<Producto> findProductosVendidosEntreFechas(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT DISTINCT p FROM Producto p JOIN DetallePedido dp ON dp.producto.id = p.id JOIN VentaDetalle vd ON vd.producto.id = p.id")
    List<Producto> findProductosPedidosYVendidos();

    @Query(value = "SELECT p.id AS producto_id, p.nombre AS producto_nombre, p.marca AS producto_marca, SUM(dv.cantidad) AS cantidad_total, SUM(dv.cantidad * dv.precio_unitario) AS monto_total FROM productos p INNER JOIN detalle_ventas dv ON dv.producto_id = p.id INNER JOIN ventas v ON dv.venta_id = v.id WHERE v.cliente_id = :clienteId GROUP BY p.id, p.nombre, p.marca ORDER BY cantidad_total DESC", nativeQuery = true)
    List<ProductoEstadisticaProjection> findEstadisticasProductosVendidosACliente(
            @Param("clienteId") Long clienteId);

    @Query(value = "SELECT p.id AS producto_id, p.nombre AS producto_nombre, p.marca AS producto_marca, SUM(dv.cantidad) AS cantidad_total, SUM(dv.cantidad * dv.precio_unitario) AS monto_total FROM productos p INNER JOIN detalle_ventas dv ON dv.producto_id = p.id GROUP BY p.id, p.nombre, p.marca ORDER BY cantidad_total DESC LIMIT 5", nativeQuery = true)
    List<ProductoEstadisticaProjection> findTop5ProductosMasVendidos();
}