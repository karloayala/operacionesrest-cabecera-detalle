package peru.edu.uls.ucos.operacionesrest.excepciones;

public class SinResultadosException extends RuntimeException {
    public SinResultadosException(String mensaje) { super(mensaje); }
}