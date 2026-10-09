package peru.edu.uls.ucos.operacionesrest.pago;

public record PagoRequest(
    String metodo,
    Double monto,
    Long ventaId
) {}