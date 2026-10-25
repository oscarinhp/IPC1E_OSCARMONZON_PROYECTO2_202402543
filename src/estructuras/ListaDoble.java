package estructuras;

/**
 * Lista doblemente enlazada implementada con nodos propios.
 *
 * <p>Se usa para la lista de residentes y para las filas del parqueo. Permite
 * insertar al final, buscar, actualizar, eliminar y recorrer la lista de inicio
 * a fin y de fin a inicio.</p>
 */
public class ListaDoble {

    private NodoDoble inicio;
    private NodoDoble fin;
    private int tamanio;

    /**
     * Agrega un dato al final de la lista.
     *
     * @param dato dato que se agregará
     */
    public void agregar(Object dato) {
        NodoDoble nuevo = new NodoDoble(dato);
        if (inicio == null) {
            inicio = nuevo;
            fin = nuevo;
        } else {
            fin.setSiguiente(nuevo);
            nuevo.setAnterior(fin);
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
        NodoDoble nuevo = new NodoDoble(dato);
        nuevo.setSiguiente(inicio);
        if (inicio != null) {
            inicio.setAnterior(nuevo);
        } else {
            fin = nuevo;
        }
        inicio = nuevo;
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
        NodoDoble actual = inicio;
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
     * Elimina la primera aparición del dato indicado, actualizando los enlaces
     * anterior y siguiente de los nodos vecinos.
     *
     * @param dato dato que se eliminará
     * @return {@code true} si se eliminó un nodo
     */
    public boolean eliminar(Object dato) {
        if (dato == null) {
            return false;
        }
        NodoDoble actual = inicio;

        while (actual != null && !dato.equals(actual.getDato())) {
            actual = actual.getSiguiente();
        }

        if (actual == null) {
            return false;
        }

        NodoDoble anterior = actual.getAnterior();
        NodoDoble siguiente = actual.getSiguiente();

        if (anterior == null) {
            inicio = siguiente;
        } else {
            anterior.setSiguiente(siguiente);
        }

        if (siguiente == null) {
            fin = anterior;
        } else {
            siguiente.setAnterior(anterior);
        }

        actual.setAnterior(null);
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
        NodoDoble actual = inicio;
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
        NodoDoble actual = inicio;
        for (int i = 0; i < indice; i++) {
            actual = actual.getSiguiente();
        }
        return actual.getDato();
    }

    /**
     * Copia los datos de la lista a un arreglo, de inicio a fin.
     *
     * @return arreglo con los datos
     */
    public Object[] aArreglo() {
        Object[] datos = new Object[tamanio];
        NodoDoble actual = inicio;
        int indice = 0;
        while (actual != null) {
            datos[indice] = actual.getDato();
            indice++;
            actual = actual.getSiguiente();
        }
        return datos;
    }

    /**
     * Copia los datos de la lista a un arreglo, de fin a inicio.
     *
     * @return arreglo con los datos en orden inverso
     */
    public Object[] aArregloInverso() {
        Object[] datos = new Object[tamanio];
        NodoDoble actual = fin;
        int indice = 0;
        while (actual != null) {
            datos[indice] = actual.getDato();
            indice++;
            actual = actual.getAnterior();
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

    public NodoDoble getInicio() {
        return inicio;
    }

    public NodoDoble getFin() {
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
        NodoDoble actual = inicio;
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

    /**
     * Devuelve la lista recorrida de fin a inicio.
     *
     * @return representación en texto del recorrido inverso
     */
    public String toStringInverso() {
        StringBuilder texto = new StringBuilder("[");
        NodoDoble actual = fin;
        while (actual != null) {
            texto.append(actual.getDato());
            if (actual.getAnterior() != null) {
                texto.append(", ");
            }
            actual = actual.getAnterior();
        }
        texto.append(']');
        return texto.toString();
    }
}
