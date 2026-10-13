package peru.edu.uls.ucos.operacionesrest.proveedor;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

    // Validar si un proveedor ya existe para evitar duplicados
    boolean existsByNombreIgnoreCase(String nombre);

    // Buscar proveedores por coincidencia de nombre (útil para un buscador en el frontend)
    List<Proveedor> findByNombreContainingIgnoreCase(String nombre);
}