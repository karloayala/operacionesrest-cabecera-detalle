package peru.edu.uls.ucos.operacionesrest.excepciones;

public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) { super(mensaje); }
}