package peru.edu.uls.ucos.operacionesrest.cliente;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import peru.edu.uls.ucos.operacionesrest.carrito.Carrito;
import peru.edu.uls.ucos.operacionesrest.direccion.Direccion;
import peru.edu.uls.ucos.operacionesrest.pedido.Pedido;
import peru.edu.uls.ucos.operacionesrest.venta.Venta;
import jakarta.persistence.*;

@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(unique = true, nullable = false)
    private String documento;

    @Column
    private String email;

    @JsonIgnore
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Pedido> pedidos = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Venta> ventas = new ArrayList<>();
    

    // Relación 1 a Muchos con Dirección
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Direccion> direcciones = new ArrayList<>();

    // Relación 1 a Muchos con Carrito
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Carrito> carritos = new ArrayList<>();

    public Cliente() {}

    public Cliente(String nombre, String documento, String email) {
        this.nombre = nombre;
        this.documento = documento;
        this.email = email;
    }


    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<Pedido> getPedidos() {return pedidos;}
    public void setPedidos(List<Pedido> pedidos) {this.pedidos = pedidos;}
    public List<Venta> getVentas() {return ventas;}
    public void setVentas(List<Venta> ventas) {this.ventas = ventas;}

    public List<Direccion> getDirecciones() { return direcciones; }
    public void setDirecciones(List<Direccion> direcciones) { this.direcciones = direcciones; }
    public List<Carrito> getCarritos() { return carritos; }
    public void setCarritos(List<Carrito> carritos) { this.carritos = carritos; }
}