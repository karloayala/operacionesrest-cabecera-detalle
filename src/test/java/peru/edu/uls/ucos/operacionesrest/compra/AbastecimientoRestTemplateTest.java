package peru.edu.uls.ucos.operacionesrest.compra;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import peru.edu.uls.ucos.operacionesrest.producto.Producto;
import peru.edu.uls.ucos.operacionesrest.producto.ProductoRepository;
import peru.edu.uls.ucos.operacionesrest.proveedor.Proveedor;
import peru.edu.uls.ucos.operacionesrest.proveedor.ProveedorRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional // Revierte automáticamente cualquier cambio en la BD al finalizar cada test
class AbastecimientoRestTemplateTest {

    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Autowired
    private ProductoRepository productoRepository;

    private Proveedor proveedorTest;
    private Producto productoTest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        proveedorTest = proveedorRepository.save(new Proveedor("Tech Supply", "Contacto", "12345"));
        productoTest = productoRepository.save(new Producto("Monitor 24", "LG", 150.0, 10));
    }

    @Test
    @DisplayName("ÉXITO: Debe registrar una compra completa y aumentar el stock")
    void registrarCompra_Exito() throws Exception {
        int cantidadComprada = 5;
        DetalleCompraRequest detalle = new DetalleCompraRequest(productoTest.getId(), cantidadComprada, 150.0);
        CompraRequest request = new CompraRequest(proveedorTest.getId(), List.of(detalle));

        mockMvc.perform(post("/api/compras")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        Producto prodActualizado = productoRepository.findById(productoTest.getId()).orElseThrow();
        assertThat(prodActualizado.getStock()).isEqualTo(10 + cantidadComprada);
    }

    @Test
    @DisplayName("FALLO: Debe rechazar una compra si el proveedor no existe")
    void registrarCompra_Fallo() throws Exception {
        DetalleCompraRequest detalle = new DetalleCompraRequest(productoTest.getId(), 5, 150.0);
        CompraRequest requestMal = new CompraRequest(9999L, List.of(detalle));

        mockMvc.perform(post("/api/compras")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestMal)))
                .andExpect(status().is4xxClientError());
    }
}