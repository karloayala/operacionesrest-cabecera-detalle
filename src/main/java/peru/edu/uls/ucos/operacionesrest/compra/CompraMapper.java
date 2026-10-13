package peru.edu.uls.ucos.operacionesrest.compra;

import org.springframework.stereotype.Component;
import peru.edu.uls.ucos.operacionesrest.proveedor.Proveedor;
import java.time.LocalDateTime;

@Component
public class CompraMapper {
    
    public Compra aEntidad(CompraRequest request, Proveedor proveedor) {
        Compra compra = new Compra(request.total(), request.estado(), proveedor);
        compra.setFecha(LocalDateTime.now()); // Registra la fecha y hora actual de la compra
        return compra;
    }

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