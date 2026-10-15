package peru.edu.uls.ucos.operacionesrest.planpago;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoDuplicadoException;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoNoEncontradoException;
import peru.edu.uls.ucos.operacionesrest.cuota.Cuota;
import peru.edu.uls.ucos.operacionesrest.venta.Venta;
import peru.edu.uls.ucos.operacionesrest.venta.VentaRepository;

@Service
public class PlanPagoService {

    private final PlanPagoRepository planPagoRepository;
    private final PlanPagoMapper planPagoMapper;
    private final VentaRepository ventaRepository;

    public PlanPagoService(PlanPagoRepository planPagoRepository,
                           PlanPagoMapper planPagoMapper,
                           VentaRepository ventaRepository) {
        this.planPagoRepository = planPagoRepository;
        this.planPagoMapper = planPagoMapper;
        this.ventaRepository = ventaRepository;
    }

    @Transactional(readOnly = true)
    public PlanPagoResponse consultarPorId(Long id) {
        return planPagoRepository.findById(id)
                .map(planPagoMapper::aRespuesta)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Plan de pago no encontrado con el ID: " + id));
    }

    @Transactional
    public PlanPagoResponse registrarPlanPago(PlanPagoRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Los datos del plan de pago son obligatorios.");
        }
        if (request.tipoPago() == null || request.tipoPago().isBlank()) {
            throw new IllegalArgumentException("El tipo de pago es obligatorio.");
        }
        if (request.cantCuotas() == null || request.cantCuotas() <= 0) {
            throw new IllegalArgumentException("La cantidad de cuotas debe ser mayor que cero.");
        }
        if (request.cuotas() != null && request.cuotas().size() > request.cantCuotas()) {
            throw new IllegalArgumentException("Las cuotas registradas no pueden superar la cantidad indicada.");
        }
        if (request.ventaId() == null || request.ventaId() <= 0) {
            throw new IllegalArgumentException("El ID de la venta debe ser mayor que cero.");
        }

        Venta venta = ventaRepository.findById(request.ventaId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Venta no encontrada con el ID: " + request.ventaId()));

        if (planPagoRepository.existsByVentaId(venta.getId())) {
            throw new RecursoDuplicadoException("La venta ya tiene un plan de pago.");
        }

        PlanPago planPago = planPagoMapper.aEntidad(request, venta);
        venta.agregarPlanPago(planPago);
        if (request.cuotas() != null) {
            for (PlanPagoRequest.CuotaPlanPagoRequest cuotaRequest : request.cuotas()) {
                Cuota cuota = planPagoMapper.cuotaAEntidad(cuotaRequest, planPago);
                planPago.agregarCuota(cuota);
            }
        }
        return planPagoMapper.aRespuesta(planPagoRepository.save(planPago));
    }
}
