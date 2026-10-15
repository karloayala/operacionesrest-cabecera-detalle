package peru.edu.uls.ucos.operacionesrest.venta;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;


@SpringBootTest
@AutoConfigureMockMvc
class RegistrarVentaTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    @DisplayName("Debe generar una venta al confirmar un pedido")
    void confirmarPedidoGeneraVenta() throws Exception {

        // 1. El pedido 1 debe existir, tener detalles
        // y todavía no tener una venta asociada.

        // 2. Confirmar el pedido y generar la venta
        MvcResult resultado = mockMvc.perform(
                post("/api/ventas/confirmar-pedido/1")
            )
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.pedidoId").value(1))
            .andExpect(jsonPath("$.detalles").isArray())
            .andReturn();

        // 3. Obtener el ID de la venta creada
        JsonNode respuesta = objectMapper.readTree(
            resultado.getResponse().getContentAsString()
        );

        long ventaId = respuesta.get("id").asLong();

        // 4. Consultar la venta creada
        mockMvc.perform(get("/api/ventas/" + ventaId))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(ventaId))
            .andExpect(jsonPath("$.pedidoId").value(1));
    }
}

