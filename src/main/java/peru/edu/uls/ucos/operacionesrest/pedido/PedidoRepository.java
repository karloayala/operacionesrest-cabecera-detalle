package peru.edu.uls.ucos.operacionesrest.pedido;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    boolean existsByNumeroPedido(String numeroPedido);
    List<Pedido> findByEstadoIgnoreCase(String estado);
    List<Pedido> findByClienteId(Long clienteId);
}
