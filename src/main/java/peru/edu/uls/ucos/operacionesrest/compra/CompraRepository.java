package peru.edu.uls.ucos.operacionesrest.compra;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface CompraRepository extends JpaRepository<Compra, Long> {

    // Consultas personalizadas para la gestión de abastecimiento
    List<Compra> findByProveedorId(Long proveedorId);
    
    List<Compra> findByEstadoIgnoreCase(String estado);
    
    List<Compra> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);

    // Obtener un reporte del total invertido agrupado por proveedor
    @Query("SELECT new map(p.nombre as proveedor, COUNT(c) as totalCompras, SUM(c.total) as totalInvertido) FROM Compra c JOIN c.proveedor p GROUP BY p.id, p.nombre")
    List<Map<String, Object>> obtenerEstadisticasComprasPorProveedor();
}