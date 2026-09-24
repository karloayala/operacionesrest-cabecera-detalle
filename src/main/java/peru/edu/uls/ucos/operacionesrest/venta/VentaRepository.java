package peru.edu.uls.ucos.operacionesrest.venta;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Long> {
    boolean existsByNumeroVenta(String numeroVenta);
    List<Venta> findByEstadoIgnoreCase(String estado);
    List<Venta> findByClienteId(Long clienteId);
}
