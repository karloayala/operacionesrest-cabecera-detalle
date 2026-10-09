package peru.edu.uls.ucos.operacionesrest.proveedor;

import org.springframework.stereotype.Component;

@Component
public class ProveedorMapper {
    public Proveedor aEntidad(ProveedorRequest request) {
        return new Proveedor(request.nombre(), request.contacto(), request.telefono());
    }

    public ProveedorResponse aRespuesta(Proveedor proveedor) {
        return new ProveedorResponse(proveedor.getId(), proveedor.getNombre(), proveedor.getContacto(), proveedor.getTelefono());
    }
}