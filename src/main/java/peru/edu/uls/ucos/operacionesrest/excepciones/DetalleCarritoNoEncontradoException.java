package peru.edu.uls.ucos.operacionesrest.excepciones;

public class DetalleCarritoNoEncontradoException extends RuntimeException {
    public DetalleCarritoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
