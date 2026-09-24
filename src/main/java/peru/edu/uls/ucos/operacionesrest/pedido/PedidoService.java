package peru.edu.uls.ucos.operacionesrest.pedido;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import peru.edu.uls.ucos.operacionesrest.cliente.Cliente;
import peru.edu.uls.ucos.operacionesrest.cliente.ClienteRepository;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoDuplicadoException;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoNoEncontradoException;

import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PedidoMapper pedidoMapper;
    private final ClienteRepository clienteRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                         PedidoMapper pedidoMapper,
                         ClienteRepository clienteRepository) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoMapper = pedidoMapper;
        this.clienteRepository = clienteRepository;
    }

    // 1. Consulta por ID
    @Transactional(readOnly = true)
    public PedidoResponse consultarPedidoPorId(Long id) {
        return pedidoRepository.findById(id)
                .map(pedidoMapper::aRespuesta)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado con ID: " + id));
    }

    // 2. Registro nuevo (validación de duplicados por 'numeroPedido' y existencia del cliente)
    @Transactional
    public PedidoResponse registrarProductoNuevo(PedidoRequest request) {
        if (pedidoRepository.existsByNumeroPedido(request.numeroPedido())) {
            throw new RecursoDuplicadoException("El pedido con número " + request.numeroPedido() + " ya existe.");
        }
        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Cliente no encontrado con ID: " + request.clienteId()));
        Pedido nuevoPedido = pedidoMapper.aEntidad(request, cliente);
        Pedido pedidoGuardado = pedidoRepository.save(nuevoPedido);
        return pedidoMapper.aRespuesta(pedidoGuardado);
    }

    // 3. Consulta por estado
    @Transactional(readOnly = true)
    public List<PedidoResponse> consultarPorEstado(String estado) {
        return pedidoRepository.findByEstadoIgnoreCase(estado)
                .stream()
                .map(pedidoMapper::aRespuesta)
                .toList();
    }

    // 4. Consulta por cliente (relación Cliente -> Pedido)
    @Transactional(readOnly = true)
    public List<PedidoResponse> consultarPorCliente(Long clienteId) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new RecursoNoEncontradoException("Cliente no encontrado con ID: " + clienteId);
        }
        return pedidoRepository.findByClienteId(clienteId)
                .stream()
                .map(pedidoMapper::aRespuesta)
                .toList();
    }
}
