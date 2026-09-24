package peru.edu.uls.ucos.operacionesrest.producto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "productos")
public class Producto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String nombre;
    
    @Column
    private String marca;
    
    @Column
    private Integer stock;

    public Producto() {}
    public Producto(String nombre, String marca, Integer stock) {
        this.nombre = nombre;
        this.marca = marca;
        this.stock = stock;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
}