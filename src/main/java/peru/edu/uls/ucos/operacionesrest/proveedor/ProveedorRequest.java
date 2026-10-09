package peru.edu.uls.ucos.operacionesrest.proveedor;

public record ProveedorRequest(
    String nombre,
    String contacto,
    String telefono
) {}