package peru.edu.uls.ucos.operacionesrest.pedido;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByEstadoIgnoreCase(String estado);
    List<Pedido> findByClienteId(Long clienteId);

    // 1. JPQL con parámetro: Busca todos los pedidos asociados a un cliente a partir de su número de documento.
    @Query("""
        SELECT p
        FROM Pedido p
        JOIN p.cliente c
        WHERE c.documento = :documento
        """)
    List<Pedido> buscarPorDocumentoCliente(@Param("documento") String documento);

    // 2. JPQL con parámetro: Busca un pedido específico por su ID y devuelve el pedido junto con la información
    @Query("""
        SELECT DISTINCT p
        FROM Pedido p
        LEFT JOIN FETCH p.cliente
        LEFT JOIN FETCH p.detalles d
        LEFT JOIN FETCH d.producto
        WHERE p.id = :id
        """)
    Optional<Pedido> buscarCompletoPorId(@Param("id") Long id);

    // 3. JPQL sin parámetro: Obtiene todos los pedidos junto con sus detalles y los productos asociados a cada detalle.
    @Query("""
        SELECT DISTINCT p
        FROM Pedido p
        LEFT JOIN FETCH p.detalles d
        LEFT JOIN FETCH d.producto
        """)
    List<Pedido> buscarTodosConDetallesYProductos();
}
