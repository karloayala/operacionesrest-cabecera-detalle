package peru.edu.uls.ucos.operacionesrest.cliente;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByDocumento(String documento);
    Optional<Cliente> findByDocumento(String documento);

    // 1. Consulta Nativa con parámetro (JOIN entre clientes y ventas)
    // Busca los clientes que tienen al menos una venta cuyo monto total
    // sea mayor al monto mínimo indicado. Para ello, relaciona las tablas
    // cliente y venta mediante el ID del cliente.
    @Query(value = """
        SELECT c.* 
        FROM clientes c 
        INNER JOIN ventas v ON c.id = v.cliente_id 
        WHERE v.total > :montoMinimo
        """, nativeQuery = true)
    List<Cliente> obtenerClientesConVentasMayoresA(@Param("montoMinimo") Double montoMinimo);


    // 2. Consulta Nativa con parámetro (JOIN entre clientes, pedidos y detalles)
    // Busca los clientes que compraron un producto específico.
    // Relaciona las tablas cliente, pedido y detalle_pedido para identificar
    // qué clientes tienen pedidos que contienen el producto indicado.
    // DISTINCT evita devolver al mismo cliente más de una vez.
    @Query(value = """
        SELECT DISTINCT c.* FROM clientes c 
        INNER JOIN pedidos p ON c.id = p.cliente_id 
        INNER JOIN detalle_pedidos dp ON p.id = dp.pedido_id
        WHERE dp.producto_id = :idProducto
        """, nativeQuery = true)
    List<Cliente> obtenerClientesQueCompraronProducto(@Param("idProducto") Long idProducto);
}