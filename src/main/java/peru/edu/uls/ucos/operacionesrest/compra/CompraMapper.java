package peru.edu.uls.ucos.operacionesrest.compra;

import org.springframework.stereotype.Component;

@Component
public class CompraMapper {
    
    public CompraResponse aRespuesta(Compra compra) {
        Long proveedorId = compra.getProveedor() != null ? compra.getProveedor().getId() : null;
        return new CompraResponse(
            compra.getId(),
            compra.getFecha(),
            compra.getTotal(),
            compra.getEstado(),
            proveedorId
        );
    }
}