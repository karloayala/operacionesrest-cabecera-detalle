package peru.edu.uls.ucos.operacionesrest.excepciones;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CarritoNoEncontradoException extends RuntimeException {
    
    public CarritoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}