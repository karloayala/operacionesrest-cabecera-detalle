package peru.edu.uls.ucos.operacionesrest.excepciones;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void manejarPedidoNoEncontrado_devuelveNotFound() {
        PedidoNoEncontradoException exception = new PedidoNoEncontradoException("Pedido no encontrado con ID: 1");

        ResponseEntity<Map<String, String>> response = handler.manejarPedidoNoEncontrado(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Pedido no encontrado con ID: 1", response.getBody().get("error"));
    }

    @Test
    void manejarDetallePedidoNoEncontrado_devuelveNotFound() {
        DetallePedidoNoEncontradoException exception = new DetallePedidoNoEncontradoException("Detalle de pedido no encontrado con ID: 5");

        ResponseEntity<Map<String, String>> response = handler.manejarDetallePedidoNoEncontrado(exception);

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Detalle de pedido no encontrado con ID: 5", response.getBody().get("error"));
    }
}
