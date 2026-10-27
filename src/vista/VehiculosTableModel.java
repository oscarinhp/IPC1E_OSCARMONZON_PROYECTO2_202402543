package vista;

import javax.swing.table.AbstractTableModel;

import modelo.Vehiculo;

/**
 * Tabla de vehículos respaldada por un arreglo que proviene de la lista
 * simplemente enlazada del residente. No usa colecciones de {@code java.util}.
 */
public class VehiculosTableModel extends AbstractTableModel {

    private static final String[] COLUMNAS =
            {"Placa", "Marca", "Modelo", "Color", "Tipo", "Estado", "Espacio"};

    private Vehiculo[] datos = new Vehiculo[0];

    /**
     * Reemplaza los vehículos que muestra la tabla.
     *
     * @param nuevos vehículos del residente seleccionado
     */
    public void setDatos(Vehiculo[] nuevos) {
        this.datos = nuevos == null ? new Vehiculo[0] : nuevos;
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
    public Object getValueAt(int fila, int columna) {
        Vehiculo vehiculo = datos[fila];
        switch (columna) {
            case 0:
                return vehiculo.getPlaca();
            case 1:
                return vehiculo.getMarca();
            case 2:
                return vehiculo.getModelo();
            case 3:
                return vehiculo.getColor();
            case 4:
                return vehiculo.getTipo().getEtiqueta();
            case 5:
                return vehiculo.getEstado().getEtiqueta();
            case 6:
                return vehiculo.getEspacioAsignado() == null
                        ? "-"
                        : vehiculo.getEspacioAsignado().getId();
            default:
                return "";
        }
    }

    /**
     * Vehículo en una fila.
     *
     * @param fila fila de la tabla
     * @return el vehículo o {@code null} si la fila no existe
     */
    public Vehiculo en(int fila) {
        if (fila < 0 || fila >= datos.length) {
            return null;
        }
        return datos[fila];
    }
}
