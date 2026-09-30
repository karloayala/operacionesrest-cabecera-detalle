package peru.edu.uls.ucos.operacionesrest.venta;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Long> {
    boolean existsByNumeroVenta(String numeroVenta);
    List<Venta> findByEstadoIgnoreCase(String estado);
    List<Venta> findByClienteId(Long clienteId);

    // 7. Consulta JPQL con parámetro: Busca todas las ventas realizadas por un cliente utilizando su número de documento.

    // 8. Consulta JPQL con parámetro: Busca todas las ventas que contienen un producto específico en alguno de sus detalles.

    // 9. Consulta JPQL sin parámetro: Obtiene todas las ventas junto con sus detalles y los productos asociados a cada detalle.

}
