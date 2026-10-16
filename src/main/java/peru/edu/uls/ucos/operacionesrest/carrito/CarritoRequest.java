package peru.edu.uls.ucos.operacionesrest.carrito;

import java.util.List;

public record CarritoRequest(
    String estado,
    Long clienteId,
    List<CarritoItemRequest> detalles
) {}