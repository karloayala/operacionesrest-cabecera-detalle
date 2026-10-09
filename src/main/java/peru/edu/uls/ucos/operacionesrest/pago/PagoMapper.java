package peru.edu.uls.ucos.operacionesrest.pago;

import org.springframework.stereotype.Component;
import peru.edu.uls.ucos.operacionesrest.venta.Venta;

@Component
public class PagoMapper {

    public Pago aEntidad(PagoRequest request, Venta venta) {
        return new Pago(
            request.metodo(),
            request.monto(),
            null,
            venta
        );
    }

    public PagoResponse aRespuesta(Pago pago) {
        return new PagoResponse(
            pago.getId(),
            pago.getMetodo(),
            pago.getMonto(),
            pago.getFechaPago(),
            pago.getVenta() == null ? null : pago.getVenta().getId()
        );
    }
}