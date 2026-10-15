package peru.edu.uls.ucos.operacionesrest.cuota;

import org.springframework.stereotype.Component;
import peru.edu.uls.ucos.operacionesrest.planpago.PlanPago;

@Component
public class CuotaMapper {

    public Cuota aEntidad(CuotaRequest request, PlanPago planPago) {
        Cuota cuota = new Cuota();
        cuota.setNumeroCuota(request.numeroCuota());
        cuota.setMonto(request.monto());
        cuota.setFechaVencimiento(request.fechaVencimiento());
        cuota.setEstado(request.estado());
        cuota.setPlanPago(planPago);
        return cuota;
    }

    public CuotaResponse aRespuesta(Cuota cuota) {
        return new CuotaResponse(
                cuota.getId(),
                cuota.getNumeroCuota(),
                cuota.getMonto(),
                cuota.getFechaVencimiento(),
                cuota.getEstado(),
                cuota.getPlanPago() == null ? null : cuota.getPlanPago().getId(),
                cuota.getPago() == null ? null : cuota.getPago().getId()
        );
    }
}
