package peru.edu.uls.ucos.operacionesrest.venta;

import peru.edu.uls.ucos.operacionesrest.ventadetalle.VentaDetalleResponse;

import java.time.LocalDateTime;
import java.util.List;

public record VentaResponse(
    Long id,
    Long pedidoId,
    LocalDateTime fecha,
    Double total,
    String estado,
    Long clienteId,
    String clienteNombre,
    String clienteDocumento,
    String clienteEmail,
    List<VentaDetalleResponse> detalles
) {}
