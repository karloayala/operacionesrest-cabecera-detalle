package peru.edu.uls.ucos.operacionesrest.excepciones;

public class RecursoDuplicadoException extends RuntimeException {
    public RecursoDuplicadoException(String mensaje) { super(mensaje); }
}
