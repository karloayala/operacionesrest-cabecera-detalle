package peru.edu.uls.ucos.operacionesrest.cuota;

import jakarta.persistence.*;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoDuplicadoException;
import peru.edu.uls.ucos.operacionesrest.pago.Pago;
import peru.edu.uls.ucos.operacionesrest.planpago.PlanPago;

import java.time.LocalDateTime;

@Entity
@Table(name = "cuotas")
public class Cuota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_cuota", nullable = false)
    private int numeroCuota;

    @Column(nullable = false)
    private double monto;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDateTime fechaVencimiento;

    @Column(nullable = false)
    private String estado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plan_pago_id", nullable = false)
    private PlanPago planPago;

    @OneToOne(mappedBy = "cuota", cascade = CascadeType.ALL, orphanRemoval = true)
    private Pago pago;

    public Cuota() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getNumeroCuota() { return numeroCuota; }
    public void setNumeroCuota(int numeroCuota) {
        if (numeroCuota <= 0) {
            throw new IllegalArgumentException("El número de cuota debe ser mayor que cero.");
        }
        this.numeroCuota = numeroCuota;
    }

    public double getMonto() { return monto; }
    public void setMonto(double monto) {
        if (!Double.isFinite(monto) || monto <= 0) {
            throw new IllegalArgumentException("El monto de la cuota debe ser un número finito mayor que cero.");
        }
        this.monto = monto;
    }

    public LocalDateTime getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDateTime fechaVencimiento) {
        if (fechaVencimiento == null) {
            throw new IllegalArgumentException("La fecha de vencimiento es obligatoria.");
        }
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getEstado() { return estado; }
    public void setEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            throw new IllegalArgumentException("El estado de la cuota es obligatorio.");
        }
        this.estado = estado;
    }

    public PlanPago getPlanPago() { return planPago; }
    public void setPlanPago(PlanPago planPago) {
        if (planPago == null) {
            throw new IllegalArgumentException("La cuota debe estar asociada a un plan de pago.");
        }
        this.planPago = planPago;
    }

    public Pago getPago() { return pago; }
    public void setPago(Pago pago) {
        if (pago != null && this.pago != null && this.pago != pago) {
            throw new RecursoDuplicadoException("La cuota ya tiene un pago registrado.");
        }
        this.pago = pago;
        if (pago != null) {
            pago.setCuota(this);
        }
    }
}
