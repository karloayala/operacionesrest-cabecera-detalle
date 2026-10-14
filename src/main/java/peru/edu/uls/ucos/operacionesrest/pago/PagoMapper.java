package peru.edu.uls.ucos.operacionesrest.pago;

import org.springframework.stereotype.Component;
import peru.edu.uls.ucos.operacionesrest.cuota.Cuota;

@Component
public class PagoMapper {

    public Pago aEntidad(PagoRequest request, Cuota cuota) {
        Pago pago = new Pago();
        pago.setMetodo(request.metodo());
        pago.setMonto(request.monto());
        pago.setCuota(cuota);
        return pago;
    }

    public PagoResponse aRespuesta(Pago pago) {
        return new PagoResponse(
            pago.getId(),
            pago.getMetodo(),
            pago.getMonto(),
            pago.getFechaPago(),
            pago.getCuota() == null ? null : pago.getCuota().getId()
        );
    }
}