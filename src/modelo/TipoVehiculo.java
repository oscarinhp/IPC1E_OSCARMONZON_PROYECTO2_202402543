package modelo;

/**
 * Tipos de vehículo que admite el parqueo.
 */
public enum TipoVehiculo {

    AUTOMOVIL("Automóvil"),
    MOTOCICLETA("Motocicleta"),
    CAMIONETA("Camioneta"),
    PICKUP("Pickup");

    private final String etiqueta;

    TipoVehiculo(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
