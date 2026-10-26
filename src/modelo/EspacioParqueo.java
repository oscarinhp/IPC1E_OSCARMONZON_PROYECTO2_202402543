package modelo;

/**
 * Espacio físico de parqueo dentro de una fila.
 *
 * <p>Guarda su identificador (por ejemplo {@code A-01}), la letra de la fila,
 * el número dentro de la fila, su tipo (socio o general) y el vehículo que lo
 * ocupa, si hay uno.</p>
 */
public class EspacioParqueo {

    private final String id;
    private final char fila;
    private final int numero;
    private final TipoEspacio tipo;
    private Vehiculo vehiculo;

    /**
     * Crea un espacio de parqueo libre.
     *
     * @param fila   letra de la fila
     * @param numero número del espacio dentro de la fila (empieza en 1)
     * @param tipo   zona a la que pertenece el espacio
     */
    public EspacioParqueo(char fila, int numero, TipoEspacio tipo) {
        this.fila = fila;
        this.numero = numero;
        this.tipo = tipo;
        this.id = fila + "-" + String.format("%02d", numero);
        this.vehiculo = null;
    }

    /**
     * Indica si el espacio está ocupado.
     *
     * @return {@code true} si hay un vehículo estacionado
     */
    public boolean estaOcupado() {
        return vehiculo != null;
    }

    /**
     * Ocupa el espacio con un vehículo.
     *
     * @param vehiculo vehículo que estaciona
     */
    public void ocupar(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    /**
     * Libera el espacio y devuelve el vehículo que lo ocupaba.
     *
     * @return el vehículo o {@code null} si el espacio ya estaba libre
     */
    public Vehiculo liberar() {
        Vehiculo vehiculoSaliente = this.vehiculo;
        this.vehiculo = null;
        return vehiculoSaliente;
    }

    public String getId() {
        return id;
    }

    public char getFila() {
        return fila;
    }

    public int getNumero() {
        return numero;
    }

    public TipoEspacio getTipo() {
        return tipo;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    @Override
    public String toString() {
        if (vehiculo != null) {
            return id + " [Ocupado: " + vehiculo.getPlaca() + "]";
        }
        return id + " [" + tipo + ", Libre]";
    }
}
