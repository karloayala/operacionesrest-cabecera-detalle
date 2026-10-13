package peru.edu.uls.ucos.operacionesrest.compra;

public record CompraRequest(
    Double total,
    String estado,
    Long proveedorId
) {}