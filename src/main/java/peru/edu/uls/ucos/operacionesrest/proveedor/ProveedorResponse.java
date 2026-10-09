package peru.edu.uls.ucos.operacionesrest.proveedor;

public record ProveedorResponse(
    Long id,
    String nombre,
    String contacto,
    String telefono
) {}