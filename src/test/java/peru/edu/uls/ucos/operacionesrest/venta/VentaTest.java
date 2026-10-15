package peru.edu.uls.ucos.operacionesrest.venta;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
class VentaTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Debe consultar una venta por su ID")
    void testConsultarVentaPorId() throws Exception {
        mockMvc.perform(get("/api/ventas/1"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.estado").exists())
            .andExpect(jsonPath("$.total").exists())
            .andExpect(jsonPath("$.pedidoId").exists());
    }
}