package peru.edu.uls.ucos.operacionesrest.detallecompra;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DetalleCompraRepository extends JpaRepository<DetalleCompra, Long> {

    // Permite listar todos los ítems de una factura de compra específica
    List<DetalleCompra> findByCompraId(Long compraId);

    // Permite rastrear todas las veces que la empresa ha adquirido un producto en particular
    List<DetalleCompra> findByProductoId(Long productoId);
}