package peru.edu.uls.ucos.operacionesrest.venta;

import peru.edu.uls.ucos.operacionesrest.ventadetalle.VentaDetalleResponse;

import java.util.List;

public record VentaResponse(
    Long id,
    String numeroVenta,
    Double total,
    String estado,
    Long clienteId,
    String clienteNombre,
    String clienteDocumento,
    List<VentaDetalleResponse> detalles
) {}
