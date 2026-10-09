package peru.edu.uls.ucos.operacionesrest.producto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

import peru.edu.uls.ucos.operacionesrest.categoria.Categoria;
import peru.edu.uls.ucos.operacionesrest.proveedor.Proveedor;
import peru.edu.uls.ucos.operacionesrest.detallepedido.DetallePedido;
import peru.edu.uls.ucos.operacionesrest.ventadetalle.VentaDetalle;
import peru.edu.uls.ucos.operacionesrest.detallecarrito.DetalleCarrito; // Import del compañero

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String marca;
    private Double precio;
    private Integer stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id")
    private Proveedor proveedor;

    @JsonIgnore
    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY)
    private List<DetallePedido> detallesPedido = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY)
    private List<VentaDetalle> detallesVenta = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY)
    private List<DetalleCarrito> detallesCarrito = new ArrayList<>();

    public Producto() {}

    public Producto(String nombre, String marca, Double precio, Integer stock) {
        this.nombre = nombre;
        this.marca = marca;
        this.precio = precio;
        this.stock = stock;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
    public Proveedor getProveedor() { return proveedor; }
    public void setProveedor(Proveedor proveedor) { this.proveedor = proveedor; }
    public List<DetallePedido> getDetallesPedido() { return detallesPedido; }
    public void setDetallesPedido(List<DetallePedido> detallesPedido) { this.detallesPedido = detallesPedido; }
    public List<VentaDetalle> getDetallesVenta() { return detallesVenta; }
    public void setDetallesVenta(List<VentaDetalle> detallesVenta) { this.detallesVenta = detallesVenta; }
    public List<DetalleCarrito> getDetallesCarrito() { return detallesCarrito; }
    public void setDetallesCarrito(List<DetalleCarrito> detallesCarrito) { this.detallesCarrito = detallesCarrito; }
}