package estructuras;

/**
 * Nodo de una lista doblemente enlazada.
 *
 * <p>Guarda un dato y referencias al nodo anterior y al siguiente. Esto permite
 * recorrer la lista en ambos sentidos y eliminar un nodo sin volver a buscar su
 * predecesor.</p>
 */
public class NodoDoble {

    private Object dato;
    private NodoDoble anterior;
    private NodoDoble siguiente;

    /**
     * Crea un nodo con el dato indicado.
     *
     * @param dato dato que se almacenará en el nodo
     */
    public NodoDoble(Object dato) {
        this.dato = dato;
    }

    public Object getDato() {
        return dato;
    }

    public void setDato(Object dato) {
        this.dato = dato;
    }

    public NodoDoble getAnterior() {
        return anterior;
    }

    public void setAnterior(NodoDoble anterior) {
        this.anterior = anterior;
    }

    public NodoDoble getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoDoble siguiente) {
        this.siguiente = siguiente;
    }

    @Override
    public String toString() {
        return String.valueOf(dato);
    }
}
