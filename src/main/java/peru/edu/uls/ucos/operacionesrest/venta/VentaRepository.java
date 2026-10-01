package peru.edu.uls.ucos.operacionesrest.venta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Long> {
    List<Venta> findByEstadoIgnoreCase(String estado);
    List<Venta> findByClienteId(Long clienteId);

    // 7. Consulta JPQL con parámetro: Busca todas las ventas realizadas por un cliente utilizando su número de documento.
    @Query("""
        SELECT v
        FROM Venta v
        JOIN v.cliente c
        WHERE c.documento = :documento
        """)
    List<Venta> buscarPorDocumentoCliente(@Param("documento") String documento);

    // 8. Consulta JPQL con parámetro: Busca todas las ventas que contienen un producto específico en alguno de sus detalles.
    @Query("""
        SELECT DISTINCT v
        FROM Venta v
        JOIN v.detalles d
        WHERE d.producto.id = :productoId
        """)
    List<Venta> buscarPorProducto(@Param("productoId") Long productoId);

    // 9. Consulta JPQL sin parámetro: Obtiene todas las ventas junto con sus detalles y los productos asociados a cada detalle.
    @Query("""
        SELECT DISTINCT v
        FROM Venta v
        LEFT JOIN FETCH v.detalles d
        LEFT JOIN FETCH d.producto
        """)
    List<Venta> buscarTodasConDetallesYProductos();
}
