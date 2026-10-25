package estructuras;

/**
 * Pila LIFO implementada con nodos propios.
 *
 * <p>Se usa para el historial de eventos del parqueo: el último evento
 * registrado es el primero en mostrarse. Los métodos son
 * {@code synchronized} porque las garitas escriben en la pila de forma
 * concurrente mientras la interfaz la lee.</p>
 */
public class Pila {

    private NodoSimple cima;
    private int tamanio;

    /**
     * Guarda un dato en la cima de la pila.
     *
     * @param dato dato que se apilará
     */
    public synchronized void apilar(Object dato) {
        NodoSimple nuevo = new NodoSimple(dato);
        nuevo.setSiguiente(cima);
        cima = nuevo;
        tamanio++;
    }

    /**
     * Quita y devuelve el dato de la cima de la pila.
     *
     * @return el dato de la cima o {@code null} si la pila está vacía
     */
    public synchronized Object desapilar() {
        if (cima == null) {
            return null;
        }
        Object dato = cima.getDato();
        cima = cima.getSiguiente();
        tamanio--;
        return dato;
    }

    /**
     * Devuelve el dato de la cima sin quitarlo.
     *
     * @return el dato de la cima o {@code null} si la pila está vacía
     */
    public synchronized Object verCima() {
        if (cima == null) {
            return null;
        }
        return cima.getDato();
    }

    /**
     * Copia los elementos de la pila a un arreglo respetando el orden LIFO.
     *
     * <p>El elemento 0 del arreglo es el más reciente y el último es el más
     * antiguo. La pila no se modifica.</p>
     *
     * @return arreglo con los elementos del más reciente al más antiguo
     */
    public synchronized Object[] aArreglo() {
        Object[] datos = new Object[tamanio];
        NodoSimple actual = cima;
        int indice = 0;
        while (actual != null) {
            datos[indice] = actual.getDato();
            indice++;
            actual = actual.getSiguiente();
        }
        return datos;
    }

    /**
     * Elimina todos los elementos de la pila.
     */
    public synchronized void limpiar() {
        cima = null;
        tamanio = 0;
    }

    public synchronized int getTamanio() {
        return tamanio;
    }

    public synchronized boolean esVacia() {
        return cima == null;
    }

    @Override
    public synchronized String toString() {
        if (cima == null) {
            return "[pila vacia]";
        }
        StringBuilder texto = new StringBuilder("[cima: ");
        texto.append(cima.getDato()).append(" | ");
        texto.append(tamanio).append(" eventos]");
        return texto.toString();
    }
}
