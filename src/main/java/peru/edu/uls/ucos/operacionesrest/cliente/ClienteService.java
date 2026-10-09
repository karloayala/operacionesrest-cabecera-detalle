package peru.edu.uls.ucos.operacionesrest.cliente;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoDuplicadoException;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoNoEncontradoException;
import peru.edu.uls.ucos.operacionesrest.excepciones.SinResultadosException;


@Service
public class ClienteService {

    private final ClienteRepository repository;
    private final ClienteMapper mapper;

    public ClienteService(ClienteRepository repository, ClienteMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public ClienteResponse registrarClienteNuevo(ClienteRequest request) {
        if (repository.existsByDocumento(request.documento())) {
            throw new RecursoDuplicadoException("Ya existe un cliente registrado con el documento: " + request.documento());
        }
        Cliente nuevoCliente = mapper.aEntidad(request);
        Cliente clienteGuardado = repository.save(nuevoCliente);
        return mapper.aRespuesta(clienteGuardado);
    }

    public ClienteResponse consultarClientePorId(Long id) {
        return repository.findById(id)
                .map(mapper::aRespuesta)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con el ID: " + id));
    }

    public ClienteResponse consultarClientePorDocumento(String documento) {
        return repository.findByDocumento(documento)
                .map(mapper::aRespuesta)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con el documento: " + documento));
    }

    public List<Map<String, Object>> obtenerClientesConVentasMayoresA(Double monto) {
        List<Object[]> resultados = repository.obtenerClientesConVentasMayoresA(monto);
        if (resultados.isEmpty()) {
            throw new SinResultadosException("No se encontraron registros de clientes con ventas mayores a: " + monto);
        }

        return resultados
            .stream()
            .map(columna -> {
                Map<String, Object> map = new HashMap<>();
                map.put("idCliente", columna[0]);
                map.put("nombre", columna[1]);
                map.put("documento", columna[2]);
                map.put("idVenta", columna[3]);
                map.put("totalVenta", columna[4]);
                return map;
            })
            .collect(Collectors.toList());
    }


    public List<Map<String, Object>> obtenerClientesQueCompraronProducto(Long idProducto) {
        List<Object[]> resultados = repository.obtenerClientesQueCompraronProducto(idProducto);
        if (resultados.isEmpty()) {
            throw new SinResultadosException("No se encontraron clientes que hayan comprado el producto con ID: " + idProducto);
        }

        return resultados
            .stream()
            .map(columna -> {
                Map<String, Object> map = new HashMap<>();
                map.put("idCliente", columna[0]);
                map.put("nombreCliente", columna[1]);
                map.put("documento", columna[2]);
                map.put("idPedido", columna[3]);
                map.put("nombreProducto", columna[4]);
                return map;
            })
            .collect(Collectors.toList());
    }
}