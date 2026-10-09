package peru.edu.uls.ucos.operacionesrest.excepciones;

public class PedidoDuplicadoException extends RuntimeException {
    public PedidoDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
