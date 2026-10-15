package peru.edu.uls.ucos.operacionesrest.planpago;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanPagoRepository extends JpaRepository<PlanPago, Long> {
    boolean existsByVentaId(Long ventaId);
}
