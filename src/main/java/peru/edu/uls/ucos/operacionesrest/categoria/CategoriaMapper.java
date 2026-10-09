package peru.edu.uls.ucos.operacionesrest.categoria;

import org.springframework.stereotype.Component;

@Component
public class CategoriaMapper {
    public Categoria aEntidad(CategoriaRequest request) {
        return new Categoria(request.nombre(), request.descripcion());
    }

    public CategoriaResponse aRespuesta(Categoria categoria) {
        return new CategoriaResponse(categoria.getId(), categoria.getNombre(), categoria.getDescripcion());
    }
}