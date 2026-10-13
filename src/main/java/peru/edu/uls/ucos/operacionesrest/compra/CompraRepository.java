package peru.edu.uls.ucos.operacionesrest.compra;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface CompraRepository extends JpaRepository<Compra, Long> {

    // Consultas personalizadas para la gestión de abastecimiento
    List<Compra> findByProveedorId(Long proveedorId);
    
    List<Compra> findByEstadoIgnoreCase(String estado);
    
    List<Compra> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);
}