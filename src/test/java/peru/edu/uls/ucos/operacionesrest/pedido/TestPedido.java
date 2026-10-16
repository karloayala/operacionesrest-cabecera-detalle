package peru.edu.uls.ucos.operacionesrest.pedido;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import peru.edu.uls.ucos.operacionesrest.detallepedido.DetallePedidoRequest;
import peru.edu.uls.ucos.operacionesrest.producto.Producto;
import peru.edu.uls.ucos.operacionesrest.producto.ProductoRepository;
import peru.edu.uls.ucos.operacionesrest.cliente.Cliente;
import peru.edu.uls.ucos.operacionesrest.cliente.ClienteRepository;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class TestPedido {

    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ProductoRepository repoProducto;
    @Autowired
    private ClienteRepository clienteRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void testConsultarPedidoPorId() throws Exception {
        mockMvc.perform(get("/api/pedidos/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testRegistrarPedido() throws Exception {
        Cliente cli = clienteRepository.save(new Cliente("Test2", "77766655", "test2@test.com"));
        Producto producto = repoProducto.save(new Producto("P2", "M", 100.0, 10));

        List<DetallePedidoRequest> detalles = List.of(
                new DetallePedidoRequest(producto.getId(), 2, 1200.50)
        );
        PedidoRequest pedidoRequest = new PedidoRequest(cli.getId(), detalles);

        mockMvc.perform(post("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedidoRequest)))
                .andExpect(status().isCreated());
    }
}