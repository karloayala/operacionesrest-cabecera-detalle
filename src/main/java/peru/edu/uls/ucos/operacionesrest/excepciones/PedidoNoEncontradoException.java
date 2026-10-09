package peru.edu.uls.ucos.operacionesrest.excepciones;

public class PedidoNoEncontradoException extends RuntimeException {
    public PedidoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
