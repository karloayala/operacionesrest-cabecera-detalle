package peru.edu.uls.ucos.operacionesrest.venta;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import peru.edu.uls.ucos.operacionesrest.cliente.Cliente;
import peru.edu.uls.ucos.operacionesrest.cliente.ClienteRepository;
import peru.edu.uls.ucos.operacionesrest.detallepedido.DetallePedido;
import peru.edu.uls.ucos.operacionesrest.pedido.EstadoPedido;
import peru.edu.uls.ucos.operacionesrest.pedido.Pedido;
import peru.edu.uls.ucos.operacionesrest.pedido.PedidoRepository;
import peru.edu.uls.ucos.operacionesrest.producto.Producto;
import peru.edu.uls.ucos.operacionesrest.producto.ProductoRepository;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class VentaConcurrencyTest {

    @Autowired
    private VentaService ventaService;

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    private Cliente clienteTest;

    @BeforeEach
    void setUp() {
        // Generación dinámica de documento para evitar colisiones sin necesidad de borrar data previa
        String documentoUnico = String.valueOf(System.currentTimeMillis()).substring(5);
        clienteTest = clienteRepository.save(new Cliente("Prueba Concurrencia", documentoUnico, documentoUnico + "@test.com"));
    }

    private Pedido crearPedidoConDetalle(Producto producto, double total, int cantidad) {
        Pedido pedido = new Pedido();
        pedido.setCliente(clienteTest);
        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setTotal(total);

        DetallePedido detalle = new DetallePedido();
        detalle.setProducto(producto);
        detalle.setCantidad(cantidad);
        detalle.setPrecioUnitario(total / cantidad);
        detalle.setPedido(pedido);

        pedido.setDetalles(List.of(detalle));
        return pedidoRepository.save(pedido);
    }

    @Test
    @DisplayName("Concurrencia 1: Prevenir doble confirmación del mismo pedido (FA-002)")
    void testConcurrenciaDobleConfirmacionPedido() throws InterruptedException {
        Producto producto = productoRepository.save(new Producto("Teclado", "Marca", 50.0, 10));
        Pedido pedido = crearPedidoConDetalle(producto, 50.0, 1);
        
        int numeroHilos = 5;
        ExecutorService executor = Executors.newFixedThreadPool(numeroHilos);
        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch completado = new CountDownLatch(numeroHilos);
        
        AtomicInteger exitos = new AtomicInteger(0);
        AtomicInteger errores = new AtomicInteger(0);

        for (int i = 0; i < numeroHilos; i++) {
            executor.submit(() -> {
                try {
                    latch.await(); 
                    ventaService.confirmarPedido(pedido.getId());
                    exitos.incrementAndGet();
                } catch (Exception e) {
                    errores.incrementAndGet();
                } finally {
                    completado.countDown();
                }
            });
        }

        latch.countDown(); 
        completado.await(); 

        assertThat(exitos.get()).isEqualTo(1);
        assertThat(errores.get()).isEqualTo(4);

        Producto prodActualizado = productoRepository.findById(producto.getId()).orElseThrow();
        assertThat(prodActualizado.getStock()).isEqualTo(9); 
    }

    @Test
    @DisplayName("Concurrencia 2: Proteger integridad de stock (FA-003)")
    void testConcurrenciaStockInsuficiente() throws InterruptedException {
        Producto producto = productoRepository.save(new Producto("Mouse", "Marca", 20.0, 2));
        
        Pedido p1 = crearPedidoConDetalle(producto, 20.0, 1);
        Pedido p2 = crearPedidoConDetalle(producto, 20.0, 1);
        Pedido p3 = crearPedidoConDetalle(producto, 20.0, 1);
        
        Long[] pedidosIds = {p1.getId(), p2.getId(), p3.getId()};
        
        ExecutorService executor = Executors.newFixedThreadPool(3);
        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch completado = new CountDownLatch(3);

        AtomicInteger exitos = new AtomicInteger(0);
        AtomicInteger errores = new AtomicInteger(0);

        for (int i = 0; i < 3; i++) {
            final Long idPedido = pedidosIds[i];
            executor.submit(() -> {
                try {
                    latch.await();
                    ventaService.confirmarPedido(idPedido);
                    exitos.incrementAndGet();
                } catch (Exception e) {
                    errores.incrementAndGet();
                } finally {
                    completado.countDown();
                }
            });
        }

        latch.countDown();
        completado.await();

        assertThat(exitos.get()).isEqualTo(2);
        assertThat(errores.get()).isEqualTo(1);

        Producto prodActualizado = productoRepository.findById(producto.getId()).orElseThrow();
        assertThat(prodActualizado.getStock()).isEqualTo(0);
    }
}