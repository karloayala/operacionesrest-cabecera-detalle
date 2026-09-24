package peru.edu.uls.ucos.operacionesrest.venta;

import org.springframework.stereotype.Component;
import peru.edu.uls.ucos.operacionesrest.cliente.Cliente;
import peru.edu.uls.ucos.operacionesrest.ventadetalle.VentaDetalleMapper;
import peru.edu.uls.ucos.operacionesrest.ventadetalle.VentaDetalleResponse;

import java.util.List;

@Component
public class VentaMapper {

    private final VentaDetalleMapper detalleMapper;

    public VentaMapper(VentaDetalleMapper detalleMapper) {
        this.detalleMapper = detalleMapper;
    }

    public Venta aEntidad(VentaRequest request, Cliente cliente) {
        String estadoInicial = request.estado() == null || request.estado().isBlank()
                ? "PENDIENTE"
                : request.estado();
        return new Venta(
            request.numeroVenta(),
            0.0,
            estadoInicial,
            cliente
        );
    }

    public VentaResponse aRespuesta(Venta venta) {
        Cliente cliente = venta.getCliente();
        List<VentaDetalleResponse> detallesResp = venta.getDetalles() == null
                ? List.of()
                : venta.getDetalles().stream().map(detalleMapper::aRespuesta).toList();

        return new VentaResponse(
            venta.getId(),
            venta.getNumeroVenta(),
            venta.getTotal(),
            venta.getEstado(),
            cliente == null ? null : cliente.getId(),
            cliente == null ? null : cliente.getNombre(),
            cliente == null ? null : cliente.getDocumento(),
            detallesResp
        );
    }
}
