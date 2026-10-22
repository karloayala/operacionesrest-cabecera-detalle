package peru.edu.uls.ucos.operacionesrest.planpago;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoDuplicadoException;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoNoEncontradoException;
import peru.edu.uls.ucos.operacionesrest.cuota.Cuota;
import peru.edu.uls.ucos.operacionesrest.venta.Venta;
import peru.edu.uls.ucos.operacionesrest.venta.VentaRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
        if (request.ventaId() == null || request.ventaId() <= 0) {
            throw new IllegalArgumentException("El ID de la venta debe ser mayor que cero.");
        }

        Venta venta = ventaRepository.findById(request.ventaId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Venta no encontrada con el ID: " + request.ventaId()));

        if (planPagoRepository.existsByVentaId(venta.getId())) {
            throw new RecursoDuplicadoException("La venta ya tiene un plan de pago.");
        }
        if (venta.getTotal() == null || !Double.isFinite(venta.getTotal()) || venta.getTotal() <= 0) {
            throw new IllegalArgumentException("El total de la venta debe ser un número finito mayor que cero.");
        }
        if (venta.getFecha() == null) {
            throw new IllegalArgumentException("La fecha de la venta es obligatoria para generar los vencimientos.");
        }

        PlanPago planPago = planPagoMapper.aEntidad(request, venta);
        venta.agregarPlanPago(planPago);
        BigDecimal totalEnCentimos = BigDecimal.valueOf(venta.getTotal())
                .setScale(2, RoundingMode.HALF_UP)
                .movePointRight(2);
        BigDecimal cantidadCuotas = BigDecimal.valueOf(request.cantCuotas());
        BigDecimal centimosBase = totalEnCentimos.divideToIntegralValue(cantidadCuotas);
        int cuotasConCentimoAdicional = totalEnCentimos.remainder(cantidadCuotas).intValueExact();
        if (centimosBase.compareTo(BigDecimal.ONE) < 0) {
            throw new IllegalArgumentException(
                    "El total de la venta no alcanza para generar montos positivos para todas las cuotas.");
        }

        for (int i = 0; i < request.cantCuotas(); i++) {
            BigDecimal montoEnCentimos = centimosBase
                    .add(i < cuotasConCentimoAdicional ? BigDecimal.ONE : BigDecimal.ZERO);
            Cuota cuota = new Cuota();
            cuota.setNumeroCuota(i + 1);
            cuota.setMonto(montoEnCentimos.movePointLeft(2).doubleValue());
            cuota.setFechaVencimiento(venta.getFecha().plusMonths(i + 1L));
            cuota.setEstado("PENDIENTE");
            planPago.agregarCuota(cuota);
        }
        return planPagoMapper.aRespuesta(planPagoRepository.save(planPago));
    }
}
