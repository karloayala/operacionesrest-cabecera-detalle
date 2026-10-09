package peru.edu.uls.ucos.operacionesrest.excepciones;

public class DetallePedidoNoEncontradoException extends RuntimeException {
    public DetallePedidoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
