package peru.edu.uls.ucos.operacionesrest.direccion;

public record DireccionRequest(
    String calle,
    String ciudad,
    String codigoPostal,
    Long clienteId
) {}