package peru.edu.uls.ucos.operacionesrest.carrito;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Carrito c WHERE c.id = :id")
    Optional<Carrito> buscarPorIdConBloqueo(@Param("id") Long id);
}