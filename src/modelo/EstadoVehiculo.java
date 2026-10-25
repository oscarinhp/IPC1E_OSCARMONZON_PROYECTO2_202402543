package modelo;

/**
 * Estados por los que pasa un vehículo dentro del flujo del parqueo.
 */
public enum EstadoVehiculo {

    /** El vehículo no está dentro del parqueo. */
    FUERA("Fuera"),

    /** El vehículo está esperando en la cola de entrada. */
    EN_COLA_ENTRADA("En cola de entrada"),

    /** El vehículo tiene espacio de parqueo asignado. */
    EN_PARQUEO("En parqueo"),

    /** El vehículo está esperando en la cola de salida. */
    EN_COLA_SALIDA("En cola de salida");

    private final String etiqueta;

    EstadoVehiculo(String etiqueta) {
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
