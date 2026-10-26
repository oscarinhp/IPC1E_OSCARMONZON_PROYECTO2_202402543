package hilos;

import modelo.SistemaParqueo;

/**
 * Administra las 5 garitas del sistema: crea los hilos, los inicia y los
 * detiene.
 *
 * <p>Los hilos son daemon para que el programa termine incluso si alguna
 * garita quedó en pausa; al cerrar la aplicación además se interrumpe a todas
 * para liberar las esperas de {@code wait()}.</p>
 */
public class AdministradorGaritas {

    private final SistemaParqueo sistema;

    private final GaritaEntrada[] garitasEntrada = new GaritaEntrada[SistemaParqueo.GARITAS_ENTRADA];
    private final GaritaSalida[] garitasSalida = new GaritaSalida[SistemaParqueo.GARITAS_SALIDA];
    private final Thread[] hilos = new Thread[SistemaParqueo.GARITAS_ENTRADA
            + SistemaParqueo.GARITAS_SALIDA];

    private volatile boolean iniciadas = false;

    /**
     * Crea el administrador para un sistema dado.
     *
     * @param sistema núcleo compartido
     */
    public AdministradorGaritas(SistemaParqueo sistema) {
        this.sistema = sistema;
    }

    /**
     * Inicia las 5 garitas como hilos daemon. Si ya estaban iniciadas, no
     * vuelve a crearlas.
     */
    public synchronized void iniciar() {
        if (iniciadas) {
            return;
        }
        sistema.setActivo(true);

        for (int i = 0; i < SistemaParqueo.GARITAS_ENTRADA; i++) {
            garitasEntrada[i] = new GaritaEntrada(i + 1, sistema);
            hilos[i] = new Thread(garitasEntrada[i], "Garita-Entrada-" + (i + 1));
        }
        for (int i = 0; i < SistemaParqueo.GARITAS_SALIDA; i++) {
            garitasSalida[i] = new GaritaSalida(i + 1, sistema);
            hilos[SistemaParqueo.GARITAS_ENTRADA + i] =
                    new Thread(garitasSalida[i], "Garita-Salida-" + (i + 1));
        }

        for (Thread hilo : hilos) {
            hilo.setDaemon(true);
            hilo.start();
        }
        iniciadas = true;
    }

    /**
     * Detiene todas las garitas e interrumpe sus esperas.
     */
    public synchronized void detener() {
        if (!iniciadas) {
            return;
        }
        sistema.setActivo(false);
        for (Thread hilo : hilos) {
            if (hilo != null && hilo.isAlive()) {
                hilo.interrupt();
            }
        }
        iniciadas = false;
    }

    public GaritaEntrada[] getGaritasEntrada() {
        return garitasEntrada;
    }

    public GaritaSalida[] getGaritasSalida() {
        return garitasSalida;
    }

    public boolean estaIniciada() {
        return iniciadas;
    }
}
