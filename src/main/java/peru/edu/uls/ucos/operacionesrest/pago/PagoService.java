package peru.edu.uls.ucos.operacionesrest.pago;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoNoEncontradoException;
import peru.edu.uls.ucos.operacionesrest.venta.Venta;
import peru.edu.uls.ucos.operacionesrest.venta.VentaRepository;

import java.time.LocalDateTime;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;
    private final PagoMapper pagoMapper;
    private final VentaRepository ventaRepository;

    public PagoService(PagoRepository pagoRepository, PagoMapper pagoMapper, VentaRepository ventaRepository) {
        this.pagoRepository = pagoRepository;
        this.pagoMapper = pagoMapper;
        this.ventaRepository = ventaRepository;
    }

    @Transactional(readOnly = true)
    public PagoResponse consultarPorId(Long id) {
        return pagoRepository.findById(id)
                .map(pagoMapper::aRespuesta)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pago no encontrado con el ID: " + id));
    }

    @Transactional
    public PagoResponse registrarPago(PagoRequest request) {
        Venta venta = ventaRepository.findById(request.ventaId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Venta no encontrada con ID: " + request.ventaId()));

        Pago pago = pagoMapper.aEntidad(request, venta);
        pago.setFechaPago(LocalDateTime.now());
        return pagoMapper.aRespuesta(pagoRepository.save(pago));
    }
}