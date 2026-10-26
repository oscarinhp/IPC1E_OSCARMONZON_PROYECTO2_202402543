package modelo;

import estructuras.ListaDoble;
import estructuras.NodoDoble;
import estructuras.NodoSimple;

/**
 * Parqueo del residencial, implementado como una lista doblemente enlazada de
 * listas simples.
 *
 * <p>El nodo exterior guarda una {@link FilaParqueo}; los enlaces
 * anterior/siguiente de esa lista son los enlaces "arriba/abajo" entre filas,
 * y cada fila lleva su propia {@code ListaSimple} de espacios. Se crean las
 * filas A, B y C con 25 espacios cada una (socios) y las filas E, F, G, H
 * e I con 15 espacios cada una (general), para un total de 150 espacios.</p>
 */
public class Parqueo {

    /** Espacios por fila en el área de socios (A, B, C). */
    public static final int ESPACIOS_POR_FILA_SOCIO = 25;

    /** Espacios por fila en el área general (E a I). */
    public static final int ESPACIOS_POR_FILA_GENERAL = 15;

    private final ListaDoble filas;
    private final int totalEspacios;

    /** Crea el parqueo con su distribución de filas y espacios. */
    public Parqueo() {
        filas = new ListaDoble();

        for (char fila : new char[]{'A', 'B', 'C'}) {
            agregarFila(fila, TipoEspacio.SOCIO, ESPACIOS_POR_FILA_SOCIO);
        }
        for (char fila : new char[]{'E', 'F', 'G', 'H', 'I'}) {
            agregarFila(fila, TipoEspacio.GENERAL, ESPACIOS_POR_FILA_GENERAL);
        }
        totalEspacios = obtenerTotalEspacios();
    }

    /**
     * Agrega una fila con el número de espacios indicado a la lista doble.
     *
     * @param letra           letra de la fila
     * @param tipo            zona de la fila
     * @param espaciosPorFila cantidad de espacios
     */
    private void agregarFila(char letra, TipoEspacio tipo, int espaciosPorFila) {
        FilaParqueo fila = new FilaParqueo(letra, tipo);
        for (int numero = 1; numero <= espaciosPorFila; numero++) {
            fila.agregarEspacio(new EspacioParqueo(letra, numero, tipo));
        }
        filas.agregar(fila);
    }

    private int obtenerTotalEspacios() {
        int total = 0;
        NodoDoble nodoFila = filas.getInicio();
        while (nodoFila != null) {
            FilaParqueo fila = (FilaParqueo) nodoFila.getDato();
            total += fila.getNumeroEspacios();
            nodoFila = nodoFila.getSiguiente();
        }
        return total;
    }

    /**
     * Asigna el primer espacio libre que corresponda al área autorizada del
     * vehículo.
     *
     * <p>Los vehículos de residentes socios solo pueden ocupar filas A–C; los
     * visitantes y los vehículos de residentes no socios solo pueden ocupar
     * filas E–I. Si no hay espacios disponibles en su zona, se devuelve
     * {@code null} y el vehículo es rechazado.</p>
     *
     * @param vehiculo vehículo que solicita estacionar
     * @return el espacio asignado o {@code null} si no hay espacio
     */
    public synchronized EspacioParqueo asignarEspacio(Vehiculo vehiculo) {
        if (vehiculo == null || vehiculo.getEspacioAsignado() != null) {
            return null;
        }

        TipoEspacio zonaSolicitada = vehiculo.puedeUsarAreaSocios()
                ? TipoEspacio.SOCIO
                : TipoEspacio.GENERAL;

        NodoDoble nodoFila = filas.getInicio();
        while (nodoFila != null) {
            FilaParqueo fila = (FilaParqueo) nodoFila.getDato();
            if (fila.getTipo() == zonaSolicitada) {
                NodoSimple nodoEspacio = fila.getEspacios().getInicio();
                while (nodoEspacio != null) {
                    EspacioParqueo espacio = (EspacioParqueo) nodoEspacio.getDato();
                    if (!espacio.estaOcupado()) {
                        espacio.ocupar(vehiculo);
                        vehiculo.setEspacioAsignado(espacio);
                        return espacio;
                    }
                    nodoEspacio = nodoEspacio.getSiguiente();
                }
            }
            nodoFila = nodoFila.getSiguiente();
        }
        return null;
    }

    /**
     * Busca el espacio donde está estacionado un vehículo por su placa.
     *
     * @param placa placa del vehículo (sin distinguir mayúsculas)
     * @return el espacio o {@code null} si no está estacionado
     */
    public synchronized EspacioParqueo buscarEspacioDeVehiculo(String placa) {
        if (placa == null) {
            return null;
        }
        String placaBuscada = placa.toUpperCase();
        NodoDoble nodoFila = filas.getInicio();
        while (nodoFila != null) {
            FilaParqueo fila = (FilaParqueo) nodoFila.getDato();
            NodoSimple nodoEspacio = fila.getEspacios().getInicio();
            while (nodoEspacio != null) {
                EspacioParqueo espacio = (EspacioParqueo) nodoEspacio.getDato();
                if (espacio.estaOcupado()
                        && espacio.getVehiculo().getPlaca().equals(placaBuscada)) {
                    return espacio;
                }
                nodoEspacio = nodoEspacio.getSiguiente();
            }
            nodoFila = nodoFila.getSiguiente();
        }
        return null;
    }

    /**
     * Libera el espacio ocupado por el vehículo con la placa indicada.
     *
     * @param placa placa del vehículo que sale
     * @return el vehículo que salió o {@code null} si no estaba estacionado
     */
    public synchronized Vehiculo liberarEspacio(String placa) {
        EspacioParqueo espacio = buscarEspacioDeVehiculo(placa);
        if (espacio == null) {
            return null;
        }
        Vehiculo vehiculo = espacio.liberar();
        if (vehiculo != null) {
            vehiculo.setEspacioAsignado(null);
        }
        return vehiculo;
    }

    /**
     * Copia los 150 espacios a un arreglo recorriendo las filas en orden y los
     * espacios de cada fila.
     *
     * @return arreglo con todos los espacios del parqueo
     */
    public synchronized EspacioParqueo[] espaciosEnOrden() {
        EspacioParqueo[] espacios = new EspacioParqueo[totalEspacios];
        int indice = 0;
        NodoDoble nodoFila = filas.getInicio();
        while (nodoFila != null) {
            FilaParqueo fila = (FilaParqueo) nodoFila.getDato();
            NodoSimple nodoEspacio = fila.getEspacios().getInicio();
            while (nodoEspacio != null) {
                espacios[indice] = (EspacioParqueo) nodoEspacio.getDato();
                indice++;
                nodoEspacio = nodoEspacio.getSiguiente();
            }
            nodoFila = nodoFila.getSiguiente();
        }
        return espacios;
    }

    /**
     * Cuenta los espacios libres de una zona.
     *
     * @param tipo zona (socio o general)
     * @return cantidad de espacios libres
     */
    public synchronized int contarLibres(TipoEspacio tipo) {
        return contarEspacios(tipo, false);
    }

    /**
     * Cuenta los espacios ocupados de una zona.
     *
     * @param tipo zona (socio o general)
     * @return cantidad de espacios ocupados
     */
    public synchronized int contarOcupados(TipoEspacio tipo) {
        return contarEspacios(tipo, true);
    }

    /**
     * Cuenta los espacios totales de una zona.
     *
     * @param tipo zona (socio o general)
     * @return cantidad total de espacios de la zona
     */
    public synchronized int contarTotales(TipoEspacio tipo) {
        int total = 0;
        NodoDoble nodoFila = filas.getInicio();
        while (nodoFila != null) {
            FilaParqueo fila = (FilaParqueo) nodoFila.getDato();
            if (fila.getTipo() == tipo) {
                total += fila.getNumeroEspacios();
            }
            nodoFila = nodoFila.getSiguiente();
        }
        return total;
    }

    private int contarEspacios(TipoEspacio tipo, boolean ocupados) {
        int total = 0;
        NodoDoble nodoFila = filas.getInicio();
        while (nodoFila != null) {
            FilaParqueo fila = (FilaParqueo) nodoFila.getDato();
            if (fila.getTipo() != tipo) {
                nodoFila = nodoFila.getSiguiente();
                continue;
            }
            NodoSimple nodoEspacio = fila.getEspacios().getInicio();
            while (nodoEspacio != null) {
                EspacioParqueo espacio = (EspacioParqueo) nodoEspacio.getDato();
                if (espacio.estaOcupado() == ocupados) {
                    total++;
                }
                nodoEspacio = nodoEspacio.getSiguiente();
            }
            nodoFila = nodoFila.getSiguiente();
        }
        return total;
    }

    public ListaDoble getFilas() {
        return filas;
    }

    public int getTotalEspacios() {
        return totalEspacios;
    }
}
