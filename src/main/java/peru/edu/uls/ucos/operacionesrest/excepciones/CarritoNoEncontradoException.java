package peru.edu.uls.ucos.operacionesrest.excepciones;

public class CarritoNoEncontradoException extends RuntimeException {
    public CarritoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
