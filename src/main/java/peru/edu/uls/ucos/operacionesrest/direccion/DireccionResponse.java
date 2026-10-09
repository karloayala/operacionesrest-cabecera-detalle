package peru.edu.uls.ucos.operacionesrest.direccion;

public record DireccionResponse(
    Long id,
    String calle,
    String ciudad,
    String codigoPostal,
    Long clienteId
) {} 