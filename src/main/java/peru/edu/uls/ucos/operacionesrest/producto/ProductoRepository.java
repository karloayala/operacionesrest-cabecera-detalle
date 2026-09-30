package peru.edu.uls.ucos.operacionesrest.producto;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    boolean existsByNombreIgnoreCase(String nombre);
    List<Producto> findByMarcaIgnoreCase(String marca);

    // 4. JPQL con parámetro: Buscar productos solicitados en pedidos de un cliente específico (Producto + DetallePedido + Pedido + Cliente)
    // 5. JPQL con parámetro: Obtener productos vendidos en un rango de fechas de venta (Producto + VentaDetalle + Venta)
    // 6. JPQL sin parámetro: Listar productos que han sido incluidos en al menos un pedido y en una venta
}