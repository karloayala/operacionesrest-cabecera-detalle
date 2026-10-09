package peru.edu.uls.ucos.operacionesrest.detallecarrito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import peru.edu.uls.ucos.operacionesrest.carrito.Carrito;
import peru.edu.uls.ucos.operacionesrest.carrito.CarritoRepository;
import peru.edu.uls.ucos.operacionesrest.excepciones.CarritoNoEncontradoException;
import peru.edu.uls.ucos.operacionesrest.producto.Producto;
import peru.edu.uls.ucos.operacionesrest.producto.ProductoRepository;

@ExtendWith(MockitoExtension.class)
class DetalleCarritoServiceTest {

    @Mock
    private DetalleCarritoRepository detalleRepository;

    @Mock
    private DetalleCarritoMapper detalleMapper;

    @Mock
    private CarritoRepository carritoRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private DetalleCarritoService detalleCarritoService;

    @Test
    void agregarDetalle_lanzaExcepcionCuandoCarritoNoExiste() {
        DetalleCarritoRequest request = new DetalleCarritoRequest(99L, 10L, 2, 25.0);
        when(carritoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CarritoNoEncontradoException.class, () -> detalleCarritoService.agregarDetalle(request));
    }

    @Test
    void agregarDetalle_devuelveRespuestaCuandoDatosSonValidos() {
        DetalleCarritoRequest request = new DetalleCarritoRequest(5L, 10L, 2, 25.0);
        Carrito carrito = org.mockito.Mockito.mock(Carrito.class);
        Producto producto = org.mockito.Mockito.mock(Producto.class);
        DetalleCarrito detalle = new DetalleCarrito(carrito, producto, 2, 25.0);
        DetalleCarritoResponse esperado = new DetalleCarritoResponse(1L, 5L, 10L, "Teclado", 2, 25.0, 50.0);

        when(carritoRepository.findById(5L)).thenReturn(Optional.of(carrito));
        when(productoRepository.findById(10L)).thenReturn(Optional.of(producto));
        when(detalleMapper.aEntidad(carrito, producto, request)).thenReturn(detalle);
        when(detalleRepository.save(detalle)).thenReturn(detalle);
        when(detalleMapper.aRespuesta(detalle)).thenReturn(esperado);

        DetalleCarritoResponse actual = detalleCarritoService.agregarDetalle(request);

        assertEquals(esperado, actual);
    }
}
