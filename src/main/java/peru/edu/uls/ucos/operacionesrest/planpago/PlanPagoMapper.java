package peru.edu.uls.ucos.operacionesrest.planpago;

import org.springframework.stereotype.Component;
import peru.edu.uls.ucos.operacionesrest.cuota.Cuota;
import peru.edu.uls.ucos.operacionesrest.cuota.CuotaMapper;
import peru.edu.uls.ucos.operacionesrest.venta.Venta;

@Component
public class PlanPagoMapper {

    private final CuotaMapper cuotaMapper;

    public PlanPagoMapper(CuotaMapper cuotaMapper) {
        this.cuotaMapper = cuotaMapper;
    }

    public PlanPago aEntidad(PlanPagoRequest request, Venta venta) {
        PlanPago planPago = new PlanPago();
        planPago.setTipoPago(request.tipoPago());
        planPago.setCantCuotas(request.cantCuotas());
        planPago.setVenta(venta);
        return planPago;
    }

    public Cuota cuotaAEntidad(PlanPagoRequest.CuotaPlanPagoRequest request, PlanPago planPago) {
        Cuota cuota = new Cuota();
        cuota.setNumeroCuota(request.numeroCuota());
        cuota.setMonto(request.monto());
        cuota.setFechaVencimiento(request.fechaVencimiento());
        cuota.setEstado(request.estado());
        cuota.setPlanPago(planPago);
        return cuota;
    }

    public PlanPagoResponse aRespuesta(PlanPago planPago) {
        return new PlanPagoResponse(
                planPago.getId(),
                planPago.getTipoPago(),
                planPago.getCantCuotas(),
                planPago.getVenta() == null ? null : planPago.getVenta().getId(),
                planPago.getCuotas().stream().map(cuotaMapper::aRespuesta).toList()
        );
    }
}
