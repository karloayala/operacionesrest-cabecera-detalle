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
    
    }
}