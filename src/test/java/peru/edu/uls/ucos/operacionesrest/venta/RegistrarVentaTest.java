package peru.edu.uls.ucos.operacionesrest.venta;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
class RegistrarVentaTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Debe crear una venta al confirmar un pedido")
    void testRegistrarVentaConDetalles() throws Exception {

        mockMvc.perform(post("/api/ventas/confirmar-pedido/1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.pedidoId").value(1))
                .andExpect(jsonPath("$.detalles").isArray());
    }
}