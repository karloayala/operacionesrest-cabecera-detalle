package peru.edu.uls.ucos.operacionesrest.pedido;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import peru.edu.uls.ucos.operacionesrest.cliente.Cliente;
import peru.edu.uls.ucos.operacionesrest.cliente.ClienteRepository;
import peru.edu.uls.ucos.operacionesrest.detallepedido.DetallePedido;
import peru.edu.uls.ucos.operacionesrest.detallepedido.DetallePedidoRequest;
import peru.edu.uls.ucos.operacionesrest.producto.Producto;
import peru.edu.uls.ucos.operacionesrest.producto.ProductoRepository;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.datasource.url=jdbc:h2:mem:pedido-test;MODE=PostgreSQL",
                "spring.datasource.username=sa",
                "spring.datasource.password=",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.jpa.hibernate.ddl-auto=create-drop"
        })
@AutoConfigureTestRestTemplate
class PedidoRestTemplateIntegrationTest {

    private static final int CANT_D = 2;
    private static final int CANT = 2;
    private static final int NUM_P = 2;
    private static final double PRECIO = 25.0;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    private Cliente cliente;
    private Producto producto;

    @BeforeEach
    void prepararDatos() {
        String identificador = UUID.randomUUID().toString();
        cliente = clienteRepository.saveAndFlush(
                new Cliente("Karlo", identificador, identificador + "@example.com"));
        producto = productoRepository.saveAndFlush(
                new Producto("Laptop " + identificador, "GamingXD",
                        PRECIO, CANT_D));
    }

    @AfterEach
    void limpiarDatos() {
        if (cliente != null && cliente.getId() != null) {
            pedidoRepository.deleteAll(pedidoRepository.findByClienteId(cliente.getId()));
        }
        if (producto != null && producto.getId() != null) {
            productoRepository.deleteById(producto.getId());
        }
        if (cliente != null && cliente.getId() != null) {
            clienteRepository.deleteById(cliente.getId());
        }
    }

    @Test
    @DisplayName("Solo registra uno de dos pedidos")
    void registraUnSoloPedido() throws Exception {
        PedidoRequest pedido = new PedidoRequest(
                cliente.getId(),
                List.of(new DetallePedidoRequest(
                        producto.getId(), CANT, PRECIO)));

        CountDownLatch latchInicio = new CountDownLatch(1);
        ExecutorService executorService = Executors.newFixedThreadPool(NUM_P);
        try {
            List<Future<ResponseEntity<String>>> respuestasFuturas = new ArrayList<>();
            for (int i = 0; i < NUM_P; i++) {
                respuestasFuturas.add(executorService.submit(() -> {
                    latchInicio.await();
                    return restTemplate.postForEntity("/api/pedidos", pedido, String.class);
                }));
            }

            latchInicio.countDown();

            List<ResponseEntity<String>> respuestas = new ArrayList<>();
            for (Future<ResponseEntity<String>> respuestaFutura : respuestasFuturas) {
                respuestas.add(respuestaFutura.get(10, TimeUnit.SECONDS));
            }

            long pedidosExitosos = respuestas.stream()
                    .filter(respuesta -> respuesta.getStatusCode().is2xxSuccessful())
                    .count();
            assertThat(pedidosExitosos).isEqualTo(1);
            assertThat(respuestas).extracting(ResponseEntity::getStatusCode)
                    .containsExactlyInAnyOrder(HttpStatus.CREATED, HttpStatus.BAD_REQUEST);

            List<Pedido> pedidosGuardados = pedidoRepository.findByClienteId(cliente.getId());
            assertThat(pedidosGuardados).hasSize(1);
            PedidoResponse pedidoGuardado = restTemplate.getForObject(
                    "/api/pedidos/{id}", PedidoResponse.class, pedidosGuardados.getFirst().getId());
            assertThat(pedidoGuardado).isNotNull();
            assertThat(pedidoGuardado.detalles()).hasSize(1);
            assertThat(pedidoGuardado.detalles().getFirst().pedidoId()).isEqualTo(pedidoGuardado.id());
            assertThat(pedidoGuardado.detalles().getFirst().productoId()).isEqualTo(producto.getId());
            assertThat(productoRepository.findById(producto.getId()).orElseThrow().getStock()).isZero();
        } finally {
            executorService.shutdownNow();
        }
    }

    @Test
    @DisplayName("Sin bloqueo, dos solicitudes")
    void sinBloqueoDosPedidos() throws Exception {
        PedidoRequest pedido = new PedidoRequest(
                cliente.getId(),
                List.of(new DetallePedidoRequest(
                        producto.getId(), CANT, PRECIO)));

        CountDownLatch latchInicio = new CountDownLatch(1);
        ExecutorService executorService = Executors.newFixedThreadPool(NUM_P);
        try {
            List<Future<ResponseEntity<PedidoResponse>>> respuestasFuturas = new ArrayList<>();
            for (int i = 0; i < NUM_P; i++) {
                respuestasFuturas.add(executorService.submit(() -> {
                    latchInicio.await();
                    return restTemplate.postForEntity(
                            "/__test/pedidos/sin-lock", pedido, PedidoResponse.class);
                }));
            }

            latchInicio.countDown();

            List<ResponseEntity<PedidoResponse>> respuestas = new ArrayList<>();
            for (Future<ResponseEntity<PedidoResponse>> respuestaFutura : respuestasFuturas) {
                respuestas.add(respuestaFutura.get(10, TimeUnit.SECONDS));
            }

            assertThat(respuestas).extracting(ResponseEntity::getStatusCode)
                    .containsExactlyInAnyOrder(HttpStatus.CREATED, HttpStatus.CREATED);
            assertThat(respuestas).allSatisfy(respuesta -> {
                assertThat(respuesta.getBody()).isNotNull();
                assertThat(respuesta.getBody().detalles()).hasSize(1);
            });

            List<Pedido> pedidosGuardados = pedidoRepository.findByClienteId(cliente.getId());
            assertThat(pedidosGuardados).hasSize(2);
            assertThat(respuestas.stream()
                    .flatMap(respuesta -> respuesta.getBody().detalles().stream())
                    .mapToInt(detalle -> detalle.cantidad())
                    .sum()).isEqualTo(CANT * NUM_P);
            assertThat(productoRepository.findById(producto.getId()).orElseThrow().getStock())
                    .isZero();
        } finally {
            executorService.shutdownNow();
        }
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class NoLockTestConfiguration {

        @Bean
        CyclicBarrier noLockRequestsBarrier() {
            return new CyclicBarrier(NUM_P);
        }

        @Bean
        NoLockPedidoTestController noLockPedidoTestController(
                ProductoRepository productoRepository,
                ClienteRepository clienteRepository,
                PedidoRepository pedidoRepository,
                PedidoMapper pedidoMapper,
                CyclicBarrier noLockRequestsBarrier) {
            return new NoLockPedidoTestController(
                    productoRepository, clienteRepository, pedidoRepository,
                    pedidoMapper, noLockRequestsBarrier);
        }
    }

    @RestController
    static class NoLockPedidoTestController {

        private final ProductoRepository productoRepository;
        private final ClienteRepository clienteRepository;
        private final PedidoRepository pedidoRepository;
        private final PedidoMapper pedidoMapper;
        private final CyclicBarrier concurrentReads;

        NoLockPedidoTestController(
                ProductoRepository productoRepository,
                ClienteRepository clienteRepository,
                PedidoRepository pedidoRepository,
                PedidoMapper pedidoMapper,
                CyclicBarrier concurrentReads) {
            this.productoRepository = productoRepository;
            this.clienteRepository = clienteRepository;
            this.pedidoRepository = pedidoRepository;
            this.pedidoMapper = pedidoMapper;
            this.concurrentReads = concurrentReads;
        }

        @PostMapping("/__test/pedidos/sin-lock")
        ResponseEntity<PedidoResponse> crearPedidoSinBloqueo(@RequestBody PedidoRequest request)
                throws InterruptedException, BrokenBarrierException, TimeoutException {
            if (request.detalles() == null || request.detalles().size() != 1) {
                throw new IllegalArgumentException("La prueba sin bloqueo requiere un solo detalle.");
            }

            var detalleRequest = request.detalles().getFirst();
            Producto producto = productoRepository.findById(detalleRequest.productoId())
                    .orElseThrow();
            if (producto.getStock() == null || producto.getStock() < detalleRequest.cantidad()) {
                throw new IllegalArgumentException("Stock insuficiente para el producto.");
            }

            concurrentReads.await(10, TimeUnit.SECONDS);

            producto.setStock(producto.getStock() - detalleRequest.cantidad());
            productoRepository.saveAndFlush(producto);

            Cliente cliente = clienteRepository.findById(request.clienteId()).orElseThrow();
            double precio = detalleRequest.precioUnitario() == null
                    ? producto.getPrecio()
                    : detalleRequest.precioUnitario();
            Pedido pedido = new Pedido(detalleRequest.cantidad() * precio, cliente);
            pedido.setFecha(java.time.LocalDateTime.now());
            pedido.setEstado(EstadoPedido.PENDIENTE);
            pedido.agregarDetalle(new DetallePedido(
                    pedido, producto, detalleRequest.cantidad(), precio));

            Pedido pedidoGuardado = pedidoRepository.saveAndFlush(pedido);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(pedidoMapper.aRespuesta(pedidoGuardado));
        }
    }
}
