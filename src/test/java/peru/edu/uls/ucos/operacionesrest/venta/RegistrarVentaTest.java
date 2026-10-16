package peru.edu.uls.ucos.operacionesrest.venta;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class RegistrarVentaTest {

    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }
    
    @Test
    @DisplayName("Debe manejar el error si el pedido no existe al confirmar venta")
    void confirmarPedidoGeneraVenta() throws Exception {
        mockMvc.perform(post("/api/ventas/confirmar-pedido/9999"))
            .andExpect(status().isNotFound());
    }
}