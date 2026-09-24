package peru.edu.uls.ucos.operacionesrest.producto;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    boolean existsByNombreIgnoreCase(String nombre);
    
    List<Producto> findByMarcaIgnoreCase(String marca);
}