package estructuras;

/**
 * Cola FIFO implementada con nodos propios.
 *
 * <p>Se usa para la cola de entrada de vehículos y para la cola de salida.
 * Los métodos que tocan la cola son {@code synchronized}, y {@code desencolar}
 * espera en un ciclo {@code while} mientras la cola esté vacía; cuando otro
 * hilo encola, llama a {@code notifyAll()} para despertar a las garitas.</p>
 */
public class Cola {

    private NodoSimple frente;
    private NodoSimple fin;
    private int tamanio;

    /**
     * Agrega un dato al final de la cola y despierta a los hilos que estaban
     * esperando por un elemento.
     *
     * @param dato dato que se agregará
     */
    public synchronized void encolar(Object dato) {
        NodoSimple nuevo = new NodoSimple(dato);
        if (frente == null) {
            frente = nuevo;
            fin = nuevo;
        } else {
            fin.setSiguiente(nuevo);
            fin = nuevo;
        }
        tamanio++;
        notifyAll();
    }

    /**
     * Toma el dato que está al frente de la cola.
     *
     * <p>Si la cola está vacía, el hilo llamador espera con {@code wait()}
     * dentro de un ciclo {@code while}; cuando otro hilo encola un dato,
     * {@code notifyAll()} despierta a los que esperan y el ciclo vuelve a
     * revisar la condición.</p>
     *
     * @return el dato que salió de la cola
     * @throws InterruptedException si el hilo es interrumpido mientras espera
     */
    public synchronized Object desencolar() throws InterruptedException {
        while (frente == null) {
            wait();
        }
        Object dato = frente.getDato();
        frente = frente.getSiguiente();
        if (frente == null) {
            fin = null;
        }
        tamanio--;
        return dato;
    }

    /**
     * Devuelve el dato del frente sin sacarlo de la cola.
     *
     * @return el dato o {@code null} si la cola está vacía
     */
    public synchronized Object verFrente() {
        if (frente == null) {
            return null;
        }
        return frente.getDato();
    }

    /**
     * Copia los elementos de la cola a un arreglo, del primero al último.
     *
     * <p>Orden de salida: el elemento 0 del arreglo es el próximo en ser
     * atendido.</p>
     *
     * @return arreglo con los elementos en orden FIFO
     */
    public synchronized Object[] aArreglo() {
        Object[] datos = new Object[tamanio];
        NodoSimple actual = frente;
        int indice = 0;
        while (actual != null) {
            datos[indice] = actual.getDato();
            indice++;
            actual = actual.getSiguiente();
        }
        return datos;
    }

    /**
     * Elimina todos los elementos de la cola.
     */
    public synchronized void limpiar() {
        frente = null;
        fin = null;
        tamanio = 0;
    }

    public synchronized int getTamanio() {
        return tamanio;
    }

    public synchronized boolean esVacia() {
        return frente == null;
    }

    @Override
    public synchronized String toString() {
        StringBuilder texto = new StringBuilder("[");
        NodoSimple actual = frente;
        while (actual != null) {
            texto.append(actual.getDato());
            if (actual.getSiguiente() != null) {
                texto.append(", ");
            }
            actual = actual.getSiguiente();
        }
        texto.append(']');
        return texto.toString();
    }
}
