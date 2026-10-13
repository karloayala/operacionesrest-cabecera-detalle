package peru.edu.uls.ucos.operacionesrest.compra;

import java.time.LocalDateTime;

public record CompraResponse(
    Long id,
    LocalDateTime fecha,
    Double total,
    String estado,
    Long proveedorId
) {}