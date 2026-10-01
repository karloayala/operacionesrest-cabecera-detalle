package peru.edu.uls.ucos.operacionesrest.producto;


public interface ProductoEstadisticaProjection {

    Long getProductoId();

    String getProductoNombre();

    String getProductoMarca();

    Integer getCantidadTotal();

    Double getMontoTotal();
}