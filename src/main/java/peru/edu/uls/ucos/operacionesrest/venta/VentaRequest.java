package peru.edu.uls.ucos.operacionesrest.venta;

import peru.edu.uls.ucos.operacionesrest.ventadetalle.VentaDetalleRequest;

import java.util.List;

public record VentaRequest(
    String numeroVenta,
    String estado,
    Long clienteId,
    List<VentaDetalleRequest> detalles
) {}
