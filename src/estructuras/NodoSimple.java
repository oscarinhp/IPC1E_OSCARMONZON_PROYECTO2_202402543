package estructuras;

/**
 * Nodo de una lista simplemente enlazada.
 *
 * <p>Guarda un dato y una referencia al siguiente nodo. El último nodo de la
 * lista tiene {@code siguiente == null}.</p>
 */
public class NodoSimple {

    private Object dato;
    private NodoSimple siguiente;

    /**
     * Crea un nodo con el dato indicado.
     *
     * @param dato dato que se almacenará en el nodo
     */
    public NodoSimple(Object dato) {
        this.dato = dato;
    }

    public Object getDato() {
        return dato;
    }

    public void setDato(Object dato) {
        this.dato = dato;
    }

    public NodoSimple getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoSimple siguiente) {
        this.siguiente = siguiente;
    }

    @Override
    public String toString() {
        return String.valueOf(dato);
    }
}
