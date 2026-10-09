package peru.edu.uls.ucos.operacionesrest.pago;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import peru.edu.uls.ucos.operacionesrest.venta.Venta;

import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String metodo;

    @Column(nullable = false)
    private Double monto;

    @Column(nullable = false)
    private LocalDateTime fechaPago;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "venta_id", nullable = false)
    @JsonIgnore
    private Venta venta;

    public Pago() {}

    public Pago(String metodo, Double monto, LocalDateTime fechaPago, Venta venta) {
        this.metodo = metodo;
        this.monto = monto;
        this.fechaPago = fechaPago;
        this.venta = venta;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMetodo() { return metodo; }
    public void setMetodo(String metodo) { this.metodo = metodo; }

    public Double getMonto() { return monto; }
    public void setMonto(Double monto) { this.monto = monto; }

    public LocalDateTime getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDateTime fechaPago) { this.fechaPago = fechaPago; }

    public Venta getVenta() { return venta; }
    public void setVenta(Venta venta) { this.venta = venta; }
}