package peru.edu.uls.ucos.operacionesrest.cliente;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByDocumento(String documento);

    Optional<Cliente> findByDocumento(String documento);
}