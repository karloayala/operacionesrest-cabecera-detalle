package peru.edu.uls.ucos.operacionesrest.planpago;

import jakarta.persistence.*;
import peru.edu.uls.ucos.operacionesrest.cuota.Cuota;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoDuplicadoException;
import peru.edu.uls.ucos.operacionesrest.venta.Venta;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "planes_pago")
public class PlanPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_pago", nullable = false)
    private String tipoPago;

    @Column(name = "cant_cuotas", nullable = false)
    private int cantCuotas;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "venta_id", nullable = false, unique = true)
    private Venta venta;

    @OneToMany(mappedBy = "planPago", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cuota> cuotas = new ArrayList<>();

    public PlanPago() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTipoPago() { return tipoPago; }
    public void setTipoPago(String tipoPago) {
        if (tipoPago == null || tipoPago.isBlank()) {
            throw new IllegalArgumentException("El tipo de pago es obligatorio.");
        }
        this.tipoPago = tipoPago;
    }

    public int getCantCuotas() { return cantCuotas; }
    public void setCantCuotas(int cantCuotas) {
        if (cantCuotas <= 0) {
            throw new IllegalArgumentException("La cantidad de cuotas debe ser mayor que cero.");
        }
        if (cantCuotas < cuotas.size()) {
            throw new IllegalArgumentException("La cantidad indicada no puede ser menor que las cuotas registradas.");
        }
        this.cantCuotas = cantCuotas;
    }

    public Venta getVenta() { return venta; }
    public void setVenta(Venta venta) {
        if (venta == null) {
            throw new IllegalArgumentException("El plan de pago debe estar asociado a una venta.");
        }
        if (venta.getPlanPago() != null && venta.getPlanPago() != this) {
            throw new RecursoDuplicadoException("La venta ya tiene un plan de pago.");
        }
        this.venta = venta;
    }

    public List<Cuota> getCuotas() { return cuotas; }
    public void setCuotas(List<Cuota> cuotas) {
        if (cuotas == null) {
            throw new IllegalArgumentException("La lista de cuotas es obligatoria.");
        }
        if (cuotas.size() > cantCuotas) {
            throw new IllegalArgumentException("Las cuotas registradas no pueden superar la cantidad indicada.");
        }
        this.cuotas = new ArrayList<>();
        for (Cuota cuota : cuotas) {
            agregarCuota(cuota);
        }
    }

    public void agregarCuota(Cuota cuota) {
        if (cuota == null) {
            throw new IllegalArgumentException("La cuota es obligatoria.");
        }
        if (cuotas.size() >= cantCuotas) {
            throw new IllegalArgumentException("El plan ya alcanzó la cantidad de cuotas indicada.");
        }
        cuotas.add(cuota);
        cuota.setPlanPago(this);
    }
}
