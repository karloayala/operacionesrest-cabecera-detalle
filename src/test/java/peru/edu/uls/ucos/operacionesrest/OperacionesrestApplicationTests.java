package peru.edu.uls.ucos.operacionesrest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

import peru.edu.uls.ucos.operacionesrest.excepciones.GlobalExceptionHandler;

class OperacionesrestApplicationTests {

    @Test
    void appStartsWithoutSpringContext() {
        assertDoesNotThrow(GlobalExceptionHandler::new);
    }
}
