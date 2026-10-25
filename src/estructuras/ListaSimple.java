package estructuras;

/**
 * Lista simplemente enlazada implementada con nodos propios.
 *
 * <p>Se usa para la lista de vehículos de cada residente. Mantiene una
 * referencia al primer nodo y al último para insertar al final en tiempo
 * constante. No utiliza ninguna colección de {@code java.util}.</p>
 */
public class ListaSimple {

    private NodoSimple inicio;
    private NodoSimple fin;
    private int tamanio;

    /**
     * Agrega un dato al final de la lista.
     *
     * @param dato dato que se agregará
     */
    public void agregar(Object dato) {
        NodoSimple nuevo = new NodoSimple(dato);
        if (inicio == null) {
            inicio = nuevo;
            fin = nuevo;
        } else {
            fin.setSiguiente(nuevo);
            fin = nuevo;
        }
        tamanio++;
    }

    /**
     * Agrega un dato al inicio de la lista.
     *
     * @param dato dato que se agregará
     */
    public void insertarAlInicio(Object dato) {
        NodoSimple nuevo = new NodoSimple(dato);
        nuevo.setSiguiente(inicio);
        inicio = nuevo;
        if (fin == null) {
            fin = nuevo;
        }
        tamanio++;
    }

    /**
     * Busca un dato usando {@link Object#equals(Object)}.
     *
     * @param dato dato buscado
     * @return el dato encontrado o {@code null} si no existe
     */
    public Object buscar(Object dato) {
        if (dato == null) {
            return null;
        }
        NodoSimple actual = inicio;
        while (actual != null) {
            if (dato.equals(actual.getDato())) {
                return actual.getDato();
            }
            actual = actual.getSiguiente();
        }
        return null;
    }

    /**
     * Indica si un dato está en la lista.
     *
     * @param dato dato buscado
     * @return {@code true} si existe
     */
    public boolean contiene(Object dato) {
        return buscar(dato) != null;
    }

    /**
     * Elimina la primera aparición del dato indicado.
     *
     * @param dato dato que se eliminará
     * @return {@code true} si se eliminó un nodo
     */
    public boolean eliminar(Object dato) {
        if (dato == null) {
            return false;
        }
        NodoSimple actual = inicio;
        NodoSimple anterior = null;

        while (actual != null && !dato.equals(actual.getDato())) {
            anterior = actual;
            actual = actual.getSiguiente();
        }

        if (actual == null) {
            return false;
        }

        if (anterior == null) {
            inicio = actual.getSiguiente();
        } else {
            anterior.setSiguiente(actual.getSiguiente());
        }

        if (actual == fin) {
            fin = anterior;
        }

        actual.setSiguiente(null);
        tamanio--;
        return true;
    }

    /**
     * Reemplaza un dato existente por otro.
     *
     * @param datoAnterior dato que se busca
     * @param datoNuevo    dato que se guardará en el mismo nodo
     * @return {@code true} si se encontró y actualizó
     */
    public boolean actualizar(Object datoAnterior, Object datoNuevo) {
        if (datoAnterior == null) {
            return false;
        }
        NodoSimple actual = inicio;
        while (actual != null) {
            if (datoAnterior.equals(actual.getDato())) {
                actual.setDato(datoNuevo);
                return true;
            }
            actual = actual.getSiguiente();
        }
        return false;
    }

    /**
     * Obtiene el dato en una posición (0 = primero).
     *
     * @param indice posición solicitada
     * @return el dato o {@code null} si el índice no es válido
     */
    public Object obtener(int indice) {
        if (indice < 0 || indice >= tamanio) {
            return null;
        }
        NodoSimple actual = inicio;
        for (int i = 0; i < indice; i++) {
            actual = actual.getSiguiente();
        }
        return actual.getDato();
    }

    /**
     * Copia los datos de la lista a un arreglo, en el mismo orden.
     *
     * @return arreglo con los datos
     */
    public Object[] aArreglo() {
        Object[] datos = new Object[tamanio];
        NodoSimple actual = inicio;
        int indice = 0;
        while (actual != null) {
            datos[indice] = actual.getDato();
            indice++;
            actual = actual.getSiguiente();
        }
        return datos;
    }

    /**
     * Elimina todos los nodos de la lista.
     */
    public void limpiar() {
        inicio = null;
        fin = null;
        tamanio = 0;
    }

    public NodoSimple getInicio() {
        return inicio;
    }

    public NodoSimple getFin() {
        return fin;
    }

    public int getTamanio() {
        return tamanio;
    }

    public boolean esVacia() {
        return inicio == null;
    }

    @Override
    public String toString() {
        StringBuilder texto = new StringBuilder("[");
        NodoSimple actual = inicio;
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
