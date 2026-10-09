package peru.edu.uls.ucos.operacionesrest.ventadetalle;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoNoEncontradoException;

@Service
public class VentaDetalleService {

    private final VentaDetalleRepository repository;
    private final VentaDetalleMapper mapper;

    public VentaDetalleService(VentaDetalleRepository repository, VentaDetalleMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public VentaDetalleResponse consultarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::aRespuesta)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Detalle de venta no encontrado con el ID: " + id));
    }
}