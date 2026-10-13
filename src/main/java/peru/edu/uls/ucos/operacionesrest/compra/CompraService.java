package peru.edu.uls.ucos.operacionesrest.compra;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import peru.edu.uls.ucos.operacionesrest.proveedor.Proveedor;
import peru.edu.uls.ucos.operacionesrest.proveedor.ProveedorRepository;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoNoEncontradoException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompraService {

    private final CompraRepository repository;
    private final ProveedorRepository proveedorRepository;
    private final CompraMapper mapper;

    public CompraService(CompraRepository repository, ProveedorRepository proveedorRepository, CompraMapper mapper) {
        this.repository = repository;
        this.proveedorRepository = proveedorRepository;
        this.mapper = mapper;
    }

    @Transactional
    public CompraResponse registrarCompra(CompraRequest request) {
        Proveedor proveedor = proveedorRepository.findById(request.proveedorId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Proveedor no encontrado con el ID: " + request.proveedorId()));
        
        Compra compra = mapper.aEntidad(request, proveedor);
        Compra guardada = repository.save(compra);
        return mapper.aRespuesta(guardada);
    }

    @Transactional(readOnly = true)
    public CompraResponse consultarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::aRespuesta)
                .orElseThrow(() -> new RecursoNoEncontradoException("Compra no encontrada con ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<CompraResponse> consultarPorProveedor(Long proveedorId) {
        if (!proveedorRepository.existsById(proveedorId)) {
            throw new RecursoNoEncontradoException("Proveedor no encontrado con ID: " + proveedorId);
        }
        return repository.findByProveedorId(proveedorId)
                .stream()
                .map(mapper::aRespuesta)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CompraResponse> consultarPorEstado(String estado) {
        return repository.findByEstadoIgnoreCase(estado)
                .stream()
                .map(mapper::aRespuesta)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CompraResponse> consultarPorRangoDeFechas(LocalDateTime inicio, LocalDateTime fin) {
        if (fin.isBefore(inicio)) {
            throw new IllegalArgumentException("La fecha final no puede ser menor a la inicial");
        }
        return repository.findByFechaBetween(inicio, fin)
                .stream()
                .map(mapper::aRespuesta)
                .collect(Collectors.toList());
    }
}