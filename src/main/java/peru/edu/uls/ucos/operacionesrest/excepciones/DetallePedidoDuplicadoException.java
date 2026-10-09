package peru.edu.uls.ucos.operacionesrest.excepciones;

public class DetallePedidoDuplicadoException extends RuntimeException {
    public DetallePedidoDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
