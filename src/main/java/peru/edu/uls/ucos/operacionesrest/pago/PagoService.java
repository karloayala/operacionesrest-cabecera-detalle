package peru.edu.uls.ucos.operacionesrest.pago;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peru.edu.uls.ucos.operacionesrest.cuota.Cuota;
import peru.edu.uls.ucos.operacionesrest.cuota.CuotaRepository;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoDuplicadoException;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoNoEncontradoException;

import java.time.LocalDateTime;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;
    private final PagoMapper pagoMapper;
    private final CuotaRepository cuotaRepository;

    public PagoService(PagoRepository pagoRepository, PagoMapper pagoMapper, CuotaRepository cuotaRepository) {
        this.pagoRepository = pagoRepository;
        this.pagoMapper = pagoMapper;
        this.cuotaRepository = cuotaRepository;
    }

    @Transactional(readOnly = true)
    public PagoResponse consultarPorId(Long id) {
        return pagoRepository.findById(id)
                .map(pagoMapper::aRespuesta)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pago no encontrado con el ID: " + id));
    }

    @Transactional
    public PagoResponse registrarPago(PagoRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Los datos del pago son obligatorios.");
        }
        if (request.metodo() == null || request.metodo().isBlank()) {
            throw new IllegalArgumentException("El método de pago es obligatorio.");
        }
        if (request.monto() == null || !Double.isFinite(request.monto()) || request.monto() <= 0) {
            throw new IllegalArgumentException("El monto del pago debe ser un número finito mayor que cero.");
        }
        if (request.cuotaId() == null || request.cuotaId() <= 0) {
            throw new IllegalArgumentException("El ID de la cuota debe ser mayor que cero.");
        }

        Cuota cuota = cuotaRepository.findById(request.cuotaId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Cuota no encontrada con ID: " + request.cuotaId()));

        if (pagoRepository.existsByCuotaId(cuota.getId())) {
            throw new RecursoDuplicadoException("La cuota ya tiene un pago registrado.");
        }

        Pago pago = pagoMapper.aEntidad(request, cuota);
        pago.setFechaPago(LocalDateTime.now());
        Pago pagoGuardado = pagoRepository.save(pago);
        cuota.setPago(pagoGuardado);
        return pagoMapper.aRespuesta(pagoGuardado);
    }
}