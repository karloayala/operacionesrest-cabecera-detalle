package peru.edu.uls.ucos.operacionesrest.direccion;

import org.springframework.stereotype.Component;

import peru.edu.uls.ucos.operacionesrest.cliente.Cliente;

@Component
public class DireccionMapper {

    public Direccion aEntidad(DireccionRequest request, Cliente cliente) {
        return new Direccion(
            request.calle(),
            request.ciudad(),
            request.codigoPostal(),
            cliente
        );
    }

    public DireccionResponse aRespuesta(Direccion direccion) {
        return new DireccionResponse(
            direccion.getId(),
            direccion.getCalle(),
            direccion.getCiudad(),
            direccion.getCodigoPostal(),
            direccion.getCliente() != null ? direccion.getCliente().getId() : null
        );
    }
}