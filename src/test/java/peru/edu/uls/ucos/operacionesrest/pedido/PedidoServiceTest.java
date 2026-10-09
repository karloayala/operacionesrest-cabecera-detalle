package peru.edu.uls.ucos.operacionesrest.pedido;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import peru.edu.uls.ucos.operacionesrest.cliente.ClienteRepository;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoNoEncontradoException;
import peru.edu.uls.ucos.operacionesrest.producto.ProductoRepository;
import peru.edu.uls.ucos.operacionesrest.producto.ProductoService;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private PedidoMapper pedidoMapper;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private ProductoService productoService;

    @InjectMocks
    private PedidoService pedidoService;

    @Test
    void consultarPedidoPorId_lanzaExcepcionCuandoNoExiste() {
        when(pedidoRepository.buscarCompletoPorId(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> pedidoService.consultarPedidoPorId(1L));
    }

    @Test
    void registrarProductoNuevo_lanzaExcepcionCuandoClienteNoExiste() {
        PedidoRequest request = new PedidoRequest("PENDIENTE", 99L, List.of());
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> pedidoService.registrarProductoNuevo(request));
    }
}
