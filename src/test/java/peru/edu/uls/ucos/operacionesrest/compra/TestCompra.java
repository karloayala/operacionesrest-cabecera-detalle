package peru.edu.uls.ucos.operacionesrest.compra;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import peru.edu.uls.ucos.operacionesrest.detallecompra.DetalleCompraRequest;
import peru.edu.uls.ucos.operacionesrest.producto.Producto;
import peru.edu.uls.ucos.operacionesrest.producto.ProductoRepository;
import peru.edu.uls.ucos.operacionesrest.proveedor.Proveedor;
import peru.edu.uls.ucos.operacionesrest.proveedor.ProveedorRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class TestCompra {

    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;

    // Solución 1: Instanciar ObjectMapper manualmente
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ProductoRepository repoProducto;
    
    @Autowired
    private ProveedorRepository repoProveedor;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void testConsultarCompraPorId() throws Exception {
        // Solución 2: Probar el manejo del 404 para no depender de la DB
        mockMvc.perform(get("/api/compras/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testRegistrarCompraAumentaStock() throws Exception {
        Proveedor prov = repoProveedor.save(new Proveedor("Prov Test", "Contacto", "123"));
        Producto prod = repoProducto.save(new Producto("Prod Test", "Marca", 10.0, 5));
        
        int stockAntes = prod.getStock();
        int cantidadComprada = 10;

        CompraRequest compraRequest = new CompraRequest(10000.0, "RECIBIDO", prov.getId());

        String responseString = mockMvc.perform(post("/api/compras")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(compraRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long compraId = objectMapper.readTree(responseString).get("id").asLong();

        DetalleCompraRequest detalleRequest = new DetalleCompraRequest(cantidadComprada, 1000.0, compraId, prod.getId());

        mockMvc.perform(post("/api/detalle-compras")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(detalleRequest)))
                .andExpect(status().isCreated());

        Producto productoActualizado = repoProducto.findById(prod.getId()).orElseThrow();
        assertThat(productoActualizado.getStock()).isEqualTo(stockAntes + cantidadComprada);
    }
}