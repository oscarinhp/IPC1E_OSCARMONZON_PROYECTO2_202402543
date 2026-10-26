package modelo;

/**
 * Tipos de evento que registra el historial del parqueo (pila LIFO).
 */
public enum TipoEvento {

    /** Un vehículo fue atendido por una garita de entrada. */
    INGRESO("Ingreso de vehículo"),

    /** Un vehículo fue atendido por una garita de salida. */
    SALIDA("Salida de vehículo"),

    /** Se asignó un espacio de parqueo a un vehículo. */
    ASIGNACION("Espacio asignado"),

    /** El vehículo no pudo estacionarse (parqueo de su zona lleno). */
    RECHAZO("Vehículo rechazado"),

    /** Se registró un visitante que solicitó ingreso. */
    VISITANTE("Visitante registrado"),

    /** El espacio del vehículo quedó libre nuevamente. */
    LIBERACION("Espacio liberado");

    private final String etiqueta;

    TipoEvento(String etiqueta) {
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
