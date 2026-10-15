package peru.edu.uls.ucos.operacionesrest.venta;

import jakarta.persistence.*;
import peru.edu.uls.ucos.operacionesrest.cliente.Cliente;
import peru.edu.uls.ucos.operacionesrest.pedido.Pedido;
import peru.edu.uls.ucos.operacionesrest.planpago.PlanPago;
import peru.edu.uls.ucos.operacionesrest.ventadetalle.VentaDetalle;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ventas")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private LocalDateTime fecha;

    @Column
    private Double total;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoVenta estado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", unique = true)
    private Pedido pedido;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VentaDetalle> detalles = new ArrayList<>();

    @OneToOne(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private PlanPago planPago;

    public Venta() {}

    public Venta(Double total, EstadoVenta estado, Cliente cliente) {
        this.total = total;
        this.estado = estado;
        this.cliente = cliente;
    }

    public void agregarDetalle(VentaDetalle detalle) {
        detalles.add(detalle);
        detalle.setVenta(this);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public EstadoVenta getEstado() { return estado; }
    public void setEstado(EstadoVenta estado) { this.estado = estado; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }

    public List<VentaDetalle> getDetalles() { return detalles; }
    public void setDetalles(List<VentaDetalle> detalles) { this.detalles = detalles; }

    public PlanPago getPlanPago() { return planPago; }
    public void setPlanPago(PlanPago planPago) {
        this.planPago = planPago;
        if (planPago != null) {
            planPago.setVenta(this);
        }
    }

    public void agregarPlanPago(PlanPago planPago) {
        setPlanPago(planPago);
    }
}
