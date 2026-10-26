package vista;

import javax.swing.table.AbstractTableModel;

import modelo.Residente;

/**
 * Tabla de residentes respaldada directamente por un arreglo que proviene de
 * la lista doblemente enlazada. No usa colecciones de {@code java.util}.
 */
public class ResidentesTableModel extends AbstractTableModel {

    private static final String[] COLUMNAS =
            {"ID", "Nombre", "Casa", "Socio", "Vehículos"};

    private Residente[] datos = new Residente[0];

    /**
     * Reemplaza los residentes que muestra la tabla.
     *
     * @param nuevos residentes del sistema (orden elegido por el panel)
     */
    public void setDatos(Residente[] nuevos) {
        this.datos = nuevos == null ? new Residente[0] : nuevos;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return datos.length;
    }

    @Override
    public int getColumnCount() {
        return COLUMNAS.length;
    }

    @Override
    public String getColumnName(int columna) {
        return COLUMNAS[columna];
    }

    @Override
    public Class<?> getColumnClass(int columna) {
        if (columna == 4) {
            return Integer.class;
        }
        return String.class;
    }

    @Override
    public Object getValueAt(int fila, int columna) {
        Residente residente = datos[fila];
        switch (columna) {
            case 0:
                return residente.getId();
            case 1:
                return residente.getNombre();
            case 2:
                return residente.getCasa();
            case 3:
                return residente.isSocio() ? "Sí" : "No";
            case 4:
                return Integer.valueOf(residente.getNumeroVehiculos());
            default:
                return "";
        }
    }

    /**
     * Residente en una fila.
     *
     * @param fila fila de la tabla
     * @return el residente o {@code null} si la fila no existe
     */
    public Residente en(int fila) {
        if (fila < 0 || fila >= datos.length) {
            return null;
        }
        return datos[fila];
    }
}
