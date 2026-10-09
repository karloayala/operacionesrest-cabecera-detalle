package peru.edu.uls.ucos.operacionesrest.detallecarrito;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleCarritoRepository extends JpaRepository<DetalleCarrito, Long> {
    List<DetalleCarrito> findByCarritoId(Long carritoId);
}
