package peru.edu.uls.ucos.operacionesrest.producto;

public record ProductoResponse(Long id, String nombre, String marca, Double precio, Integer stock) {}