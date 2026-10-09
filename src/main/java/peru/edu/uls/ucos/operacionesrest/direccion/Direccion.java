package peru.edu.uls.ucos.operacionesrest.direccion;

import jakarta.persistence.*;
import peru.edu.uls.ucos.operacionesrest.cliente.Cliente;

@Entity
@Table(name = "direccion")
public class Direccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String calle;
    private String ciudad;

    @Column(name = "codigo_postal")
    private String codigoPostal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente clienteId;

    public Direccion() {}

    public Direccion(String calle, String ciudad, String codigoPostal, Cliente clienteId) {
        this.calle = calle;
        this.ciudad = ciudad;
        this.codigoPostal = codigoPostal;
        this.clienteId = clienteId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCalle() { return calle; }
    public void setCalle(String calle) { this.calle = calle; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }

    public Cliente getCliente() { return clienteId; }
    public void setCliente(Cliente clienteId) { this.clienteId = clienteId; }
}