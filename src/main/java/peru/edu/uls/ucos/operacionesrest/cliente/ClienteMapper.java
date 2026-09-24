package peru.edu.uls.ucos.operacionesrest.cliente;

import org.springframework.stereotype.Component;

@Component
public class ClienteMapper {

    public Cliente aEntidad(ClienteRequest request) {
        return new Cliente(request.nombre(), request.documento(), request.email());
    }

    public ClienteResponse aRespuesta(Cliente cliente) {
        return new ClienteResponse(cliente.getId(), cliente.getNombre(), cliente.getDocumento(), cliente.getEmail());
    }
}