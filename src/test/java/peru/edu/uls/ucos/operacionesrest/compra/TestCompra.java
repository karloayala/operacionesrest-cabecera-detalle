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
import peru.edu.uls.ucos.operacionesrest.producto.Producto;
import peru.edu.uls.ucos.operacionesrest.producto.ProductoRepository;
import peru.edu.uls.ucos.operacionesrest.proveedor.Proveedor;
import peru.edu.uls.ucos.operacionesrest.proveedor.ProveedorRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class TestCompra {

    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;

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
        mockMvc.perform(get("/api/compras/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testRegistrarCompraAumentaStock() throws Exception {
        Proveedor prov = repoProveedor.save(new Proveedor("Prov Test", "Contacto", "123"));
        Producto prod = repoProducto.save(new Producto("Prod Test", "Marca", 10.0, 5));
        
        int stockAntes = prod.getStock();
        int cantidadComprada = 10;

        DetalleCompraRequest detalle = new DetalleCompraRequest(prod.getId(), cantidadComprada, 1000.0);
        CompraRequest compraRequest = new CompraRequest(prov.getId(), List.of(detalle));

        mockMvc.perform(post("/api/compras")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(compraRequest)))
                .andExpect(status().isCreated());

        Producto productoActualizado = repoProducto.findById(prod.getId()).orElseThrow();
        assertThat(productoActualizado.getStock()).isEqualTo(stockAntes + cantidadComprada);
    }
}