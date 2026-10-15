package peru.edu.uls.ucos.operacionesrest.pedido;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import peru.edu.uls.ucos.operacionesrest.detallepedido.DetallePedidoRequest;
import peru.edu.uls.ucos.operacionesrest.producto.Producto;
import peru.edu.uls.ucos.operacionesrest.producto.ProductoRepository;
import peru.edu.uls.ucos.operacionesrest.cliente.Cliente;
import peru.edu.uls.ucos.operacionesrest.cliente.ClienteRepository;

@SpringBootTest
class RegistrarPedidoTest {

    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;
    
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private ClienteRepository clienteRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @DisplayName("Debe registrar un pedido con sus detalles")
    void testRegistrarPedidoConDetalles() throws Exception {
        Cliente cli = clienteRepository.save(new Cliente("Test", "99988877", "test@test.com"));
        Producto producto1 = productoRepository.save(new Producto("P1", "M", 100.0, 10));
        
        List<DetallePedidoRequest> detalles = List.of(
                new DetallePedidoRequest(producto1.getId(), 1, 100.00)
        );

        PedidoRequest request = new PedidoRequest(cli.getId(), detalles);
        
        mockMvc.perform(post("/api/pedidos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());
    }
}