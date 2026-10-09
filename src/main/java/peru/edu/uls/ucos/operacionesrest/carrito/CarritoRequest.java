package peru.edu.uls.ucos.operacionesrest.carrito;

import java.time.LocalDateTime;

public record CarritoRequest(
    LocalDateTime fechaCreacion,
    String estado,
    Long clienteId
) {}