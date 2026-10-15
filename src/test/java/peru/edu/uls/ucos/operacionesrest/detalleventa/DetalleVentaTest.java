package peru.edu.uls.ucos.operacionesrest.detalleventa;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;


@SpringBootTest
@AutoConfigureMockMvc
class VentaDetalleTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Debe consultar un detalle de venta por su ID")
    void testConsultarVentaDetallePorId() throws Exception {
        mockMvc.perform(get("/api/venta-detalles/1"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.cantidad").exists())
        .andExpect(jsonPath("$.precioUnitario").exists())
        .andExpect(jsonPath("$.productoId").exists());
        }
}