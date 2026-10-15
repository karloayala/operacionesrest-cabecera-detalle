package peru.edu.uls.ucos.operacionesrest.pedido;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
import org.springframework.test.web.servlet.MvcResult;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import peru.edu.uls.ucos.operacionesrest.detallepedido.DetallePedidoRequest;
import peru.edu.uls.ucos.operacionesrest.producto.Producto;
import peru.edu.uls.ucos.operacionesrest.producto.ProductoRepository;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@AutoConfigureMockMvc
class RegistrarPedidoTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductoRepository productoRepository;

    @Test
    @DisplayName("Debe registrar un pedido con sus detalles")
    void testRegistrarPedidoConDetalles() throws Exception {
        
        Producto producto1 = productoRepository.findById(1L).orElseThrow();
        Producto producto2 = productoRepository.findById(2L).orElseThrow();
        
        int stockInicial1 = producto1.getStock();
        int stockInicial2 = producto2.getStock();


        List<DetallePedidoRequest> detalles = List.of(
                new DetallePedidoRequest(1L, 1, 100.00),
                new DetallePedidoRequest(2L, 1, 250.00)
        );

        PedidoRequest request = new PedidoRequest(
                1L,
                detalles
        );
        
        MvcResult resultado = mockMvc.perform(
                post("/api/pedidos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                )
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andReturn();

        // 4. Obtener el ID del pedido creado
        JsonNode respuesta = objectMapper.readTree(
            resultado.getResponse().getContentAsString()
        );

        long pedidoId = respuesta.get("id").asLong();

        // 5. Verificar que el pedido se pueda consultar
        mockMvc.perform(get("/api/pedidos/" + pedidoId))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(pedidoId))
            .andExpect(jsonPath("$.detalles.length()").value(2))
            .andExpect(jsonPath("$.total").value(350.00));
        
        // 6. Verificar que el stock haya disminuido.
        Producto producto1Actualizado = productoRepository.findById(1L).orElseThrow();
        Producto producto2Actualizado = productoRepository.findById(2L).orElseThrow();
        
        assertThat(producto1Actualizado.getStock()).isEqualTo(stockInicial1 - 1);
        assertThat(producto2Actualizado.getStock()).isEqualTo(stockInicial2 - 1);
        
    }
}
