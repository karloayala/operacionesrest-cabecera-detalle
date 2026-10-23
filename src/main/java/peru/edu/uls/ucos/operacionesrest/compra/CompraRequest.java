package peru.edu.uls.ucos.operacionesrest.compra;

import java.util.List;

public record CompraRequest(
    Long proveedorId,
    List<DetalleCompraRequest> detalles
) {}