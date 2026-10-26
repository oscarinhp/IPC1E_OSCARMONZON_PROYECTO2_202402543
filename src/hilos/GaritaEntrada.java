package hilos;

import modelo.SistemaParqueo;
import modelo.Vehiculo;

/**
 * Garita de entrada del parqueo. Cada garita es un hilo (implementa
 * {@link Runnable}) que toma vehículos de la cola de entrada compartida.
 *
 * <p>La espera por un vehículo se hace en {@code Cola.desencolar}, que usa
 * {@code wait()} dentro de un ciclo {@code while}; al llegar un vehículo,
 * {@code Cola.encolar} ejecuta {@code notifyAll()} y despierta a las garitas
 * que esperaban. La asignación del espacio se hace mediante
 * {@code SistemaParqueo.processEntrada}, que es synchronized, por lo que dos
 * garitas nunca asignan el mismo espacio.</p>
 */
public class GaritaEntrada implements Runnable {

    /** Milisegundos para revisar al vehículo que llegó. */
    public static final long MS_REVISION = 700;

    /** Milisegundos de trámite cuando ya obtuvo un espacio. */
    public static final long MS_TRAMITE = 900;

    private final SistemaParqueo sistema;
    private final String nombre;

    /** Placa del vehículo que la garita está atendiendo en este instante. */
    private volatile String placaActual = "—";

    /** Indica si la garita está ocupada atendiendo alguna placa. */
    private volatile boolean atendiendo = false;

    /**
     * Crea una garita de entrada.
     *
     * @param id      número de garita (1 a 3)
     * @param sistema sistema compartido
     */
    public GaritaEntrada(int id, SistemaParqueo sistema) {
        this.nombre = "Entrada-" + id;
        this.sistema = sistema;
    }

    @Override
    public void run() {
        while (sistema.isActivo()) {
            Vehiculo vehiculo;
            try {
                vehiculo = sistema.tomarDeColaEntrada();
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
                sistema.procesarEntrada(vehiculo);
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
