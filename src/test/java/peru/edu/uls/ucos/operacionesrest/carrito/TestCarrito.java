package peru.edu.uls.ucos.operacionesrest.carrito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import peru.edu.uls.ucos.operacionesrest.cliente.Cliente;
import peru.edu.uls.ucos.operacionesrest.cliente.ClienteRepository;
import peru.edu.uls.ucos.operacionesrest.detallecarrito.DetalleCarrito;
import peru.edu.uls.ucos.operacionesrest.detallecarrito.DetalleCarritoRepository;
import peru.edu.uls.ucos.operacionesrest.direccion.Direccion;
import peru.edu.uls.ucos.operacionesrest.direccion.DireccionRepository;
import peru.edu.uls.ucos.operacionesrest.producto.Producto;
import peru.edu.uls.ucos.operacionesrest.producto.ProductoRepository;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:operacionesrest-test;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.show-sql=false"
})
class TestCarrito {

    @Autowired
    private WebApplicationContext context;
    
    private MockMvc mockMvc;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private DireccionRepository direccionRepository;

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private DetalleCarritoRepository detalleCarritoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        
        detalleCarritoRepository.deleteAll();
        carritoRepository.deleteAll();
        direccionRepository.deleteAll();
        clienteRepository.deleteAll();
        productoRepository.deleteAll();
    }

    @Test
    void consultaClientePorId() throws Exception {
        Cliente cliente = crearCliente();

        mockMvc.perform(get("/api/clientes/{id}", cliente.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cliente.getId()))
                .andExpect(jsonPath("$.nombre").value("Ana Torres"));
    }

    @Test
    void consultaDireccionPorId() throws Exception {
        Cliente cliente = crearCliente();
        Direccion direccion = direccionRepository.save(
                new Direccion("Av. Ejército 123", "Arequipa", "04001", cliente));

        mockMvc.perform(get("/api/direcciones/{id}", direccion.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(direccion.getId()))
                .andExpect(jsonPath("$.clienteId").value(cliente.getId()));
    }

    @Test
    void consultaCarritoPorId() throws Exception {
        Carrito carrito = carritoRepository.save(new Carrito("ACTIVO", crearCliente()));

        mockMvc.perform(get("/api/carritos/{id}", carrito.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(carrito.getId()))
                .andExpect(jsonPath("$.estado").value("ACTIVO"));
    }

    @Test
    void consultaDetalleCarritoPorId() throws Exception {
        Carrito carrito = carritoRepository.save(new Carrito("ACTIVO", crearCliente()));
        Producto producto = crearProducto("Teclado");
        DetalleCarrito detalle = detalleCarritoRepository.save(
                new DetalleCarrito(carrito, producto, 2, 25.0));

        mockMvc.perform(get("/api/detalle-carrito/{id}", detalle.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(detalle.getId()))
                .andExpect(jsonPath("$.productoId").value(producto.getId()));
    }

    @Test
    void registraCarritoConDetallesEnUnaTransaccion() throws Exception {
        Cliente cliente = crearCliente();
        Producto teclado = crearProducto("Teclado");
        Producto raton = crearProducto("Ratón");
        String contenido = """
                {
                  "estado": "ACTIVO",
                  "clienteId": %d,
                  "detalles": [
                    {"productoId": %d, "cantidad": 2, "precioUnitario": 25.0},
                    {"productoId": %d, "cantidad": 1, "precioUnitario": 40.0}
                  ]
                }
                """.formatted(cliente.getId(), teclado.getId(), raton.getId());

        mockMvc.perform(post("/api/carritos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contenido))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());

        long carritoId = carritoRepository.findAll().getFirst().getId();
        assertTrue(carritoRepository.existsById(carritoId));

        List<DetalleCarrito> detallesGuardados = detalleCarritoRepository.findByCarritoId(carritoId);
        assertEquals(2, detallesGuardados.size());
        assertEquals(
                List.of(teclado.getId(), raton.getId()),
                detallesGuardados.stream().map(detalle -> detalle.getProducto().getId()).toList());
    }

    private Cliente crearCliente() {
        return clienteRepository.save(new Cliente("Ana Torres", "DNI-123", "ana@example.test"));
    }

    private Producto crearProducto(String nombre) {
        return productoRepository.save(new Producto(nombre, "Marca", 50.0, 10));
    }
}