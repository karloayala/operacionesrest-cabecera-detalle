package peru.edu.uls.ucos.operacionesrest.carrito;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CarritoRepository extends JpaRepository<Carrito, Long> {
  
  @Query("SELECT c FROM Carrito c JOIN c.cliente cl WHERE cl.documento = :documento AND c.estado = 'ACTIVO'")
  Optional<Carrito> buscarCarritoActivoPorDocumentoCliente(@Param("documento") String documento);
}