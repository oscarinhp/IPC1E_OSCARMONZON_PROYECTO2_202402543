package modelo;

/**
 * Tipos de espacio del parqueo según su zona.
 */
public enum TipoEspacio {

    /** Filas A–C: exclusivas para vehículos de residentes socios. */
    SOCIO("Socio (A-C)"),

    /** Filas E–I: para residentes no socios y visitantes. */
    GENERAL("General (E-I)");

    private final String etiqueta;

    TipoEspacio(String etiqueta) {
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
