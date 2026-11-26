package peru.edu.uls.ucos.operacionesrest.pedido;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import peru.edu.uls.ucos.operacionesrest.carrito.Carrito;
import peru.edu.uls.ucos.operacionesrest.carrito.CarritoRepository;
import peru.edu.uls.ucos.operacionesrest.cliente.Cliente;
import peru.edu.uls.ucos.operacionesrest.cliente.ClienteRepository;
import peru.edu.uls.ucos.operacionesrest.detallecarrito.DetalleCarrito;
import peru.edu.uls.ucos.operacionesrest.detallecarrito.DetalleCarritoRepository;
import peru.edu.uls.ucos.operacionesrest.producto.Producto;
import peru.edu.uls.ucos.operacionesrest.producto.ProductoRepository;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.datasource.url=jdbc:h2:mem:proceso-pedido-test;DB_CLOSE_DELAY=-1",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.datasource.username=sa",
                "spring.jpa.hibernate.ddl-auto=create-drop",
                "spring.jpa.show-sql=false"
        })
@AutoConfigureTestRestTemplate
class ProcesoPedidoTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private DetalleCarritoRepository detalleCarritoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Test
    @DisplayName("Proceso_CU-001")
    void procesoCU001() {
        Cliente cliente = crearCliente();
        Producto producto = productoRepository.saveAndFlush(
                new Producto("Producto de prueba", "Marca", 25.0, 10));
        Carrito carrito = carritoRepository.saveAndFlush(new Carrito("ACTIVO", cliente));
        detalleCarritoRepository.saveAndFlush(new DetalleCarrito(carrito, producto, 2, 25.0));

        ResponseEntity<PedidoResponse> response = restTemplate.postForEntity(
                "/api/pedidos/crear-pedido/{carritoId}",
                null,
                PedidoResponse.class,
                carrito.getId());

        mostrarRespuesta("Proceso_CU-001", response);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().estado()).isEqualTo(EstadoPedido.PENDIENTE);
        assertThat(response.getBody().clienteId()).isEqualTo(cliente.getId());
        assertThat(response.getBody().detalles()).hasSize(1);
        assertThat(response.getBody().detalles().getFirst().cantidad()).isEqualTo(2);
    }

    @Test
    @DisplayName("No debe crear dos pedidos al confirmar simultáneamente el mismo carrito")
    void noDebeCrearPedidosDuplicadosEnSolicitudesSimultaneas() throws Exception {
        Cliente cliente = crearCliente();
        Producto producto = productoRepository.saveAndFlush(
                new Producto("Producto concurrente", "Marca", 25.0, 10));
        Carrito carrito = carritoRepository.saveAndFlush(new Carrito("ACTIVO", cliente));
        detalleCarritoRepository.saveAndFlush(new DetalleCarrito(carrito, producto, 1, 25.0));

        long pedidosAntes = pedidoRepository.count();
        CountDownLatch inicio = new CountDownLatch(1);

        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            Future<ResponseEntity<String>> primeraSolicitud = executor.submit(() -> {
                inicio.await();
                return crearPedido(carrito.getId());
            });
            Future<ResponseEntity<String>> segundaSolicitud = executor.submit(() -> {
                inicio.await();
                return crearPedido(carrito.getId());
            });

            inicio.countDown();

            List<Integer> estados = List.of(
                    primeraSolicitud.get().getStatusCode().value(),
                    segundaSolicitud.get().getStatusCode().value());

            assertThat(estados).containsExactlyInAnyOrder(
                    HttpStatus.CREATED.value(),
                    HttpStatus.CONFLICT.value());
        }

        assertThat(pedidoRepository.count()).isEqualTo(pedidosAntes + 1);
        assertThat(carritoRepository.findById(carrito.getId()))
                .get()
                .extracting(Carrito::getEstado)
                .isEqualTo("PROCESADO");
    }

    @Test
    @DisplayName("CU-001_Alter1")
    void cu001Alter1() {
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/pedidos/crear-pedido/{carritoId}",
                null,
                String.class,
                Long.MAX_VALUE);

        mostrarRespuesta("CU-001_Alter1", response);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody())
                .contains("Carrito no encontrado con ID: " + Long.MAX_VALUE);
    }

    @Test
    @DisplayName("CU-001_Alter2")
    void cu001Alter2() {
        Carrito carritoVacio = carritoRepository.saveAndFlush(new Carrito("ACTIVO", crearCliente()));

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/pedidos/crear-pedido/{carritoId}",
                null,
                String.class,
                carritoVacio.getId());

        mostrarRespuesta("CU-001_Alter2", response);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody())
                .contains("El pedido debe contener al menos un producto.");
    }

    private void mostrarRespuesta(String test, ResponseEntity<?> response) {
        System.out.printf(
                "%s -> HTTP %s, body: %s%n",
                test,
                response.getStatusCode(),
                response.getBody());
    }

    private Cliente crearCliente() {
        return clienteRepository.saveAndFlush(new Cliente(
                "Cliente de prueba",
                "DNI-" + java.util.UUID.randomUUID(),
                "cliente@example.test"));
    }

    private ResponseEntity<String> crearPedido(Long carritoId) {
        return restTemplate.postForEntity(
                "/api/pedidos/crear-pedido/{carritoId}",
                null,
                String.class,
                carritoId);
    }
}
