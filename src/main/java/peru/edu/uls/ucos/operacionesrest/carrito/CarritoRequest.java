package peru.edu.uls.ucos.operacionesrest.carrito;

public record CarritoRequest(
    String estado,
    Long clienteId
) {}