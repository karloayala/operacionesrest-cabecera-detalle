package peru.edu.uls.ucos.operacionesrest.detallepedido;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class DetallePedidoTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Debe consultar un detalle de pedido por su ID")
    void testConsultarDetallePedidoPorId() throws Exception {
        mockMvc.perform(get("/api/detalles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }
}