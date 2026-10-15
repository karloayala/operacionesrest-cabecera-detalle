package peru.edu.uls.ucos.operacionesrest.cuota;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoNoEncontradoException;
import peru.edu.uls.ucos.operacionesrest.planpago.PlanPago;
import peru.edu.uls.ucos.operacionesrest.planpago.PlanPagoRepository;

@Service
public class CuotaService {

    private final CuotaRepository cuotaRepository;
    private final CuotaMapper cuotaMapper;
    private final PlanPagoRepository planPagoRepository;

    public CuotaService(CuotaRepository cuotaRepository,
                        CuotaMapper cuotaMapper,
                        PlanPagoRepository planPagoRepository) {
        this.cuotaRepository = cuotaRepository;
        this.cuotaMapper = cuotaMapper;
        this.planPagoRepository = planPagoRepository;
    }

    @Transactional(readOnly = true)
    public CuotaResponse consultarPorId(Long id) {
        return cuotaRepository.findById(id)
                .map(cuotaMapper::aRespuesta)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Cuota no encontrada con el ID: " + id));
    }

    @Transactional
    public CuotaResponse registrarCuota(CuotaRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Los datos de la cuota son obligatorios.");
        }
        if (request.numeroCuota() == null || request.numeroCuota() <= 0) {
            throw new IllegalArgumentException("El número de cuota debe ser mayor que cero.");
        }
        if (request.monto() == null || !Double.isFinite(request.monto()) || request.monto() <= 0) {
            throw new IllegalArgumentException("El monto de la cuota debe ser un número finito mayor que cero.");
        }
        if (request.fechaVencimiento() == null) {
            throw new IllegalArgumentException("La fecha de vencimiento es obligatoria.");
        }
        if (request.estado() == null || request.estado().isBlank()) {
            throw new IllegalArgumentException("El estado de la cuota es obligatorio.");
        }
        if (request.planPagoId() == null || request.planPagoId() <= 0) {
            throw new IllegalArgumentException("El ID del plan de pago debe ser mayor que cero.");
        }

        PlanPago planPago = planPagoRepository.findById(request.planPagoId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Plan de pago no encontrado con el ID: " + request.planPagoId()));

        Cuota cuota = cuotaMapper.aEntidad(request, planPago);
        planPago.agregarCuota(cuota);
        return cuotaMapper.aRespuesta(cuotaRepository.save(cuota));
    }
}
