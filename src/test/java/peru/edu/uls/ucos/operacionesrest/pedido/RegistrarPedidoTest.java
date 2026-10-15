package peru.edu.uls.ucos.operacionesrest.pedido;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.ObjectMapper;
import peru.edu.uls.ucos.operacionesrest.detallepedido.DetallePedidoRequest;


@SpringBootTest
@AutoConfigureMockMvc
class RegistrarPedidoTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Debe registrar un pedido con sus detalles")
    void testRegistrarPedidoConDetalles() throws Exception {

        List<DetallePedidoRequest> detalles = List.of(
                new DetallePedidoRequest(1L, 1, 100.00),
                new DetallePedidoRequest(2L, 1, 250.00)
        );

        PedidoRequest request = new PedidoRequest(
                1L,
                detalles
        );

        mockMvc.perform(post("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }
}