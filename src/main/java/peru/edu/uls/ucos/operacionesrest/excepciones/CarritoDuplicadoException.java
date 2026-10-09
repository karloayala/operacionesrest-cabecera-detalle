package peru.edu.uls.ucos.operacionesrest.excepciones;

public class CarritoDuplicadoException extends RuntimeException {
    public CarritoDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
