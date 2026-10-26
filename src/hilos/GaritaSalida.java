package hilos;

import modelo.SistemaParqueo;
import modelo.Vehiculo;

/**
 * Garita de salida del parqueo. Cada garita es un hilo (implementa
 * {@link Runnable}) que toma vehículos de la cola de salida compartida.
 *
 * <p>La espera se hace con {@code wait()} en {@code Cola.desencolar} y la
 * liberación del espacio se hace en {@code SistemaParqueo.procesarSalida},
 * que es synchronized, para evitar que se libere dos veces el mismo
 * espacio.</p>
 */
public class GaritaSalida implements Runnable {

    /** Milisegundos para revisar al vehículo que quiere salir. */
    public static final long MS_REVISION = 600;

    /** Milisegundos de trámite para liberar el espacio. */
    public static final long MS_TRAMITE = 800;

    private final SistemaParqueo sistema;
    private final String nombre;

    /** Placa del vehículo que la garita está atendiendo en este instante. */
    private volatile String placaActual = "—";

    /** Indica si la garita está ocupada atendiendo alguna placa. */
    private volatile boolean atendiendo = false;

    /**
     * Crea una garita de salida.
     *
     * @param id      número de garita (1 a 2)
     * @param sistema sistema compartido
     */
    public GaritaSalida(int id, SistemaParqueo sistema) {
        this.nombre = "Salida-" + id;
        this.sistema = sistema;
    }

    @Override
    public void run() {
        while (sistema.isActivo()) {
            Vehiculo vehiculo;
            try {
                vehiculo = sistema.tomarDeColaSalida();
            } catch (InterruptedException e) {
                return;
            }
            if (!sistema.isActivo()) {
                return;
            }
            try {
                placaActual = vehiculo.getPlaca();
                atendiendo = true;
                Thread.sleep(MS_REVISION);
                sistema.procesarSalida(vehiculo);
                Thread.sleep(MS_TRAMITE);
            } catch (InterruptedException e) {
                return;
            } finally {
                placaActual = "—";
                atendiendo = false;
                sistema.notificarCambio();
            }
        }
    }

    public String getNombre() {
        return nombre;
    }

    public String getVehiculoAtendido() {
        return placaActual;
    }

    public boolean estaAtendiendo() {
        return atendiendo;
    }
}
