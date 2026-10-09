package peru.edu.uls.ucos.operacionesrest.excepciones;

public class DetalleCarritoDuplicadoException extends RuntimeException {
    public DetalleCarritoDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
