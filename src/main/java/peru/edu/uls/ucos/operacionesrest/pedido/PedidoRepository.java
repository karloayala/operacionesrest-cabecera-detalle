package peru.edu.uls.ucos.operacionesrest.pedido;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    boolean existsByNumeroPedido(String numeroPedido);
    List<Pedido> findByEstadoIgnoreCase(String estado);
    List<Pedido> findByClienteId(Long clienteId);

    // 1. JPQL con parámetro: Busca todos los pedidos asociados a un cliente a partir de su número de documento.
    
    // 2. JPQL con parámetro: Busca un pedido específico por su ID y devuelve el pedido junto con la información
    
    // 3. JPQL sin parámetro: Obtiene todos los pedidos junto con sus detalles y los productos asociados a cada detalle.

}
