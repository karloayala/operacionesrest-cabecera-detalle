package peru.edu.uls.ucos.operacionesrest.proveedor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import peru.edu.uls.ucos.operacionesrest.compra.Compra;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "proveedores")
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String contacto;
    private String telefono;

    @JsonIgnore
    @OneToMany(mappedBy = "proveedor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Compra> compras = new ArrayList<>();

    public Proveedor() {}

    public Proveedor(String nombre, String contacto, String telefono) {
        this.nombre = nombre;
        this.contacto = contacto;
        this.telefono = telefono;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getContacto() { return contacto; }
    public void setContacto(String contacto) { this.contacto = contacto; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public List<Compra> getCompras() { return compras; }
    public void setCompras(List<Compra> compras) { this.compras = compras; }
}