package peru.edu.uls.ucos.operacionesrest.pago;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    boolean existsByCuotaId(Long cuotaId);
}