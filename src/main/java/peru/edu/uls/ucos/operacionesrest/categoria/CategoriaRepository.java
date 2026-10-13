package peru.edu.uls.ucos.operacionesrest.categoria;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    boolean existsByNombreIgnoreCase(String nombre);

    List<Categoria> findByNombreContainingIgnoreCase(String nombre);
}