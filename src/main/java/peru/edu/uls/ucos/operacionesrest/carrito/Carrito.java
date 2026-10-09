package peru.edu.uls.ucos.operacionesrest.carrito;

import jakarta.persistence.*;
import peru.edu.uls.ucos.operacionesrest.cliente.Cliente;

import java.time.LocalDateTime;

@Entity
@Table(name = "carrito")
public class Carrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    private String estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente clienteId;

    public Carrito() {}

    public Carrito(LocalDateTime fechaCreacion, String estado, Cliente clienteId) {
        this.fechaCreacion = fechaCreacion;
        this.estado = estado;
        this.clienteId = clienteId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Cliente getCliente() { return clienteId; }
    public void setCliente(Cliente clienteId) { this.clienteId = clienteId; }
}