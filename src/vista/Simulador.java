package vista;

import modelo.Residente;
import modelo.SistemaParqueo;
import modelo.TipoVehiculo;
import modelo.Vehiculo;

/**
 * Generador de escenarios de prueba para el botón de simulación.
 *
 * <p>Usa un generador lineal congruencial propio en lugar de
 * {@code java.util.Random} para no depender de colecciones de esa librería,
 * y produce nombres, marcas y colores aleatorios a partir de arreglos
 * fijos.</p>
 */
public final class Simulador {

    private static final String[] NOMBRES_RESIDENTE = {
        "Ana López", "Carlos Ruiz", "María Pérez", "Luis García",
        "Sofía Ramírez", "Pedro Tun", "Lucía Morales", "Diego Cifuentes",
        "Ana Paiz", "Roberto Leiva"
    };

    private static final String[] NOMBRES_VISITANTE = {
        "Jorge Molina", "Laura Vásquez", "Andrés Rojas", "Daniela Soto",
        "Miguel Álvarez", "Fernanda Cota", "Óscar Pineda", "Valeria Avila",
        "Hugo Arriaga", "Silvia Barrientos"
    };

    private static final String[] MARCAS = {
        "Toyota", "Honda", "Mazda", "Kia", "Hyundai", "Nissan", "Suzuki"
    };

    private static final String[] MODELOS = {
        " Hilux 2020", " Civic 2019", " 3 2021", " Rio 2018",
        " Tucson 2022", " Sentra 2020", " Vitara 2019"
    };

    private static final String[] COLORES = {
        "Rojo", "Azul", "Blanco", "Negro", "Plata", "Verde", "Gris"
    };

    private static final TipoVehiculo[] TIPOS = {
        TipoVehiculo.MOTOCICLETA, TipoVehiculo.AUTOMOVIL
    };

    private static long semilla = System.currentTimeMillis() | 1L;
    private static int contador = 0;

    private Simulador() {
    }

    /**
     * Siguiente valor pseudoaleatorio entre 0 y limite-1 (generador lineal
     * congruencial simple).
     *
     * @param limite valor exclusivo máximo
     * @return entero pseudoaleatorio
     */
    private static int aleatorio(int limite) {
        semilla = semilla * 1103515245L + 12345L;
        int valor = (int) ((semilla >>> 17) % limite);
        if (valor < 0) {
            valor = -valor;
        }
        return valor;
    }

    /**
     * Genera una placa única (la repite hasta que el sistema la acepte).
     *
     * @param sistema núcleo para validar duplicados
     * @return placa nueva
     */
    private static String generarPlaca(SistemaParqueo sistema) {
        String placa;
        do {
            contador++;
            char letra1 = (char) ('A' + aleatorio(26));
            char letra2 = (char) ('A' + aleatorio(26));
            char letra3 = (char) ('A' + aleatorio(26));
            placa = "" + letra1 + letra2 + letra3 + "-" + (100 + contador);
        } while (sistema.buscarVehiculoPorPlaca(placa) != null);
        return placa;
    }

    /**
     * Registra a un visitante con datos aleatorios y lo manda a la cola de
     * entrada (llegada → cola → garita → espacio).
     *
     * @param sistema núcleo
     * @return descripción de lo simulado
     */
    public static String simularVisitante(SistemaParqueo sistema) {
        int numero = (int) (System.currentTimeMillis() % 100000);
        String nombre = NOMBRES_VISITANTE[aleatorio(NOMBRES_VISITANTE.length)]
                + " (v" + numero + ")";
        String placa = generarPlaca(sistema);
        Vehiculo visitante = sistema.registrarVisitante(nombre, placa,
                MARCAS[aleatorio(MARCAS.length)],
                MODELOS[aleatorio(MODELOS.length)],
                COLORES[aleatorio(COLORES.length)],
                TIPOS[aleatorio(TIPOS.length)]);
        sistema.solicitarEntrada(visitante.getPlaca());
        return "Visitante " + visitante.getPlaca() + " en cola de entrada.";
    }

    /**
     * Registra a un residente con datos aleatorios, le registra un vehículo y
     * lo manda a la cola de entrada.
     *
     * @param sistema núcleo
     * @return descripción de lo simulado
     */
    public static String simularResidente(SistemaParqueo sistema) {
        int numero = (int) (System.currentTimeMillis() % 100000);
        Residente residente = sistema.registrarResidente(
                NOMBRES_RESIDENTE[aleatorio(NOMBRES_RESIDENTE.length)]
                        + " (r" + numero + ")",
                "Casa " + (1 + aleatorio(120)),
                aleatorio(100) < 40);
        String placa = generarPlaca(sistema);
        sistema.registrarVehiculo(residente.getId(), placa,
                MARCAS[aleatorio(MARCAS.length)],
                MODELOS[aleatorio(MODELOS.length)],
                COLORES[aleatorio(COLORES.length)],
                TIPOS[aleatorio(TIPOS.length)]);
        sistema.solicitarEntrada(placa);
        return "Residente " + residente.getId() + " (" + residente.getNombre()
                + ") con placa " + placa + " en cola de entrada.";
    }

    /**
     * Solicita la salida de un vehículo estacionado elegido al azar.
     *
     * @param sistema núcleo
     * @return descripción de lo simulado o {@code null} si no hay estacionados
     */
    public static String simularSalida(SistemaParqueo sistema) {
        Vehiculo[] estacionados = sistema.vehiculosEstacionados();
        if (estacionados.length == 0) {
            return null;
        }
        Vehiculo vehiculo = estacionados[aleatorio(estacionados.length)];
        sistema.solicitarSalida(vehiculo.getPlaca());
        return "Salida solicitada para " + vehiculo.getPlaca() + ".";
    }
}
