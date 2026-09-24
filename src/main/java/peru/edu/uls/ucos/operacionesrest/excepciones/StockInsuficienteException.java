package peru.edu.uls.ucos.operacionesrest.excepciones;

public class StockInsuficienteException extends RuntimeException {
    public StockInsuficienteException(String mensaje) {
        super(mensaje);
    }
}
