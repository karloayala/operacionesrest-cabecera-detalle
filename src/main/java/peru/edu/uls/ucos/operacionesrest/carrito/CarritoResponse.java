package peru.edu.uls.ucos.operacionesrest.carrito;

import java.time.LocalDateTime;

public record CarritoResponse(
    Long id,
    LocalDateTime fechaCreacion,
    String estado,
    Long clienteId
) {}