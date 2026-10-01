package peru.edu.uls.ucos.operacionesrest.producto;

public record ProductoEstadisticaResponse(
        Long productoId,
        String productoNombre,
        String productoMarca,
        Integer cantidadTotal,
        Double montoTotal
) {
}