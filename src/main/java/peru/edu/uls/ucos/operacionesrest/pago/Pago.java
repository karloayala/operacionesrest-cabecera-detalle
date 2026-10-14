package peru.edu.uls.ucos.operacionesrest.pago;

import jakarta.persistence.*;
import peru.edu.uls.ucos.operacionesrest.cuota.Cuota;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoDuplicadoException;

import java.time.LocalDateTime;

@Entity
@Table(name = "PAGO")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String metodo;

    @Column(nullable = false)
    private double monto;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cuota_id", nullable = false, unique = true)
    private Cuota cuota;

    public Pago() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMetodo() { return metodo; }
    public void setMetodo(String metodo) {
        if (metodo == null || metodo.isBlank()) {
            throw new IllegalArgumentException("El método de pago es obligatorio.");
        }
        this.metodo = metodo;
    }

    public double getMonto() { return monto; }
    public void setMonto(double monto) {
        if (!Double.isFinite(monto) || monto <= 0) {
            throw new IllegalArgumentException("El monto del pago debe ser un número finito mayor que cero.");
        }
        this.monto = monto;
    }

    public LocalDateTime getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDateTime fechaPago) {
        if (fechaPago == null) {
            throw new IllegalArgumentException("La fecha de pago es obligatoria.");
        }
        this.fechaPago = fechaPago;
    }

    public Cuota getCuota() { return cuota; }
    public void setCuota(Cuota cuota) {
        if (cuota == null) {
            throw new IllegalArgumentException("El pago debe estar asociado a una cuota.");
        }
        if (cuota.getPago() != null && cuota.getPago() != this) {
            throw new RecursoDuplicadoException("La cuota ya tiene un pago registrado.");
        }
        this.cuota = cuota;
    }
}
