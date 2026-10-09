package peru.edu.uls.ucos.operacionesrest.carrito;

import org.springframework.stereotype.Component;

import peru.edu.uls.ucos.operacionesrest.cliente.Cliente;

@Component
public class CarritoMapper {

    public Carrito aEntidad(CarritoRequest request, Cliente cliente) {
        return new Carrito(
            request.fechaCreacion(),
            request.estado(),
            cliente
        );
    }

    public CarritoResponse aRespuesta(Carrito carrito) {
        return new CarritoResponse(
            carrito.getId(),
            carrito.getFechaCreacion(),
            carrito.getEstado(),
            carrito.getCliente() != null ? carrito.getCliente().getId() : null
        );
    }
}