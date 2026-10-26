package modelo;

import estructuras.ListaSimple;

/**
 * Fila del parqueo: guarda la letra, el tipo de zona y la lista simple de
 * espacios que la componen.
 *
 * <p>Las filas se guardan dentro de una lista doblemente enlazada en
 * {@link Parqueo}, así que los enlaces anterior/siguiente de cada fila son los
 * enlaces "arriba/abajo" entre filas.</p>
 */
public class FilaParqueo {

    private final char letra;
    private final TipoEspacio tipo;
    private final ListaSimple espacios;

    /**
     * Crea una fila vacía de la letra y tipo indicados.
     *
     * @param letra letra de la fila
     * @param tipo  zona de la fila
     */
    public FilaParqueo(char letra, TipoEspacio tipo) {
        this.letra = letra;
        this.tipo = tipo;
        this.espacios = new ListaSimple();
    }

    /**
     * Agrega un espacio a la lista de la fila.
     *
     * @param espacio espacio que se agrega
     */
    public void agregarEspacio(EspacioParqueo espacio) {
        espacios.agregar(espacio);
    }

    public char getLetra() {
        return letra;
    }

    public TipoEspacio getTipo() {
        return tipo;
    }

    public ListaSimple getEspacios() {
        return espacios;
    }

    /**
     * Cantidad de espacios de esta fila.
     *
     * @return tamaño de la lista de espacios
     */
    public int getNumeroEspacios() {
        return espacios.getTamanio();
    }

    @Override
    public String toString() {
        return "Fila " + letra + " [" + tipo + ", " + getNumeroEspacios() + " espacios]";
    }
}
