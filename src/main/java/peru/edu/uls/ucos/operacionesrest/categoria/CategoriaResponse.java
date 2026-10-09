package peru.edu.uls.ucos.operacionesrest.categoria;

public record CategoriaResponse(
    Long id,
    String nombre,
    String descripcion
) {}