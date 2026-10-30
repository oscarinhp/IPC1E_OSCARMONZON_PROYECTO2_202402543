import java.awt.Component;
import java.awt.Container;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import modelo.SistemaParqueo;
import modelo.TipoVehiculo;
import modelo.Vehiculo;
import vista.PanelEntrada;
import vista.PanelResidentes;
import vista.PanelVehiculos;

/**
 * Regresion del alta R015 -> selector de Vehiculos -> vehiculo disponible en
 * Entrada. Usa paneles reales y el mismo invokeLater de Main, sin archivos ni
 * ventanas de dialogo. Las comprobaciones de Swing se ejecutan en el EDT.
 */
public class VerificarRefrescoResidentes {
    private final SistemaParqueo sistema = new SistemaParqueo();
    private PanelResidentes residentes;
    private PanelVehiculos vehiculos;
    private PanelEntrada entrada;
    private int notificaciones;
    private int comprobaciones;

    public static void main(String[] args) throws Exception {
        new VerificarRefrescoResidentes().ejecutar();
    }

    private void ejecutar() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            residentes = new PanelResidentes(sistema);
            vehiculos = new PanelVehiculos(sistema);
            entrada = new PanelEntrada(sistema);
            sistema.setObservador(() -> SwingUtilities.invokeLater(() -> {
                notificaciones++;
                residentes.actualizar();
                vehiculos.actualizar();
                entrada.actualizar();
            }));
            comprobar(combo(vehiculos).getItemCount() == 0, "Selector inicialmente vacio");
            sistema.registrarResidenteConId("R015", "Oscar Lopez", "15", false);
        });
        // Cada invocacion espera tambien los refrescos ya encolados en el EDT.
        SwingUtilities.invokeAndWait(() -> {
            comprobar(combo(vehiculos).getItemCount() == 1
                    && combo(vehiculos).getSelectedItem().toString().contains("R015 - Oscar Lopez"),
                    "R015 aparece en Vehiculos sin reiniciar ni refrescar manualmente");
            comprobar(buscar(residentes, JTable.class).getRowCount() == 1,
                    "Tabla de Residentes actualizada tras alta");
            comprobar(notificaciones == 1, "Alta con ID emite una notificacion");
            sistema.registrarResidente("Otro residente", "16", false);
        });
        SwingUtilities.invokeAndWait(() -> {
            comprobar(combo(vehiculos).getItemCount() == 2 && notificaciones == 2,
                    "Alta con ID automatico tambien refresca");
            comprobar(combo(vehiculos).getSelectedItem().toString().contains("R015"),
                    "Se conserva la seleccion del residente al agregar otro");
            sistema.actualizarResidente("R015", "Oscar Lopez actualizado", "15", false);
        });
        SwingUtilities.invokeAndWait(() -> {
            comprobar(notificaciones == 3
                    && combo(vehiculos).getSelectedItem().toString().contains("Oscar Lopez actualizado")
                    && "Oscar Lopez actualizado".equals(buscar(residentes, JTable.class).getValueAt(0, 1)),
                    "Edicion refresca tabla y selector");
            sistema.registrarVehiculo("R015", "DOC015", "Toyota", "Corolla", "Blanco",
                    TipoVehiculo.AUTOMOVIL);
        });
        SwingUtilities.invokeAndWait(() -> {
            comprobar(buscar(vehiculos, JTable.class).getRowCount() == 1
                    && sistema.vehiculosDeResidente("R015").length == 1,
                    "Vehiculo asociado a R015 y visible en su tabla");
            JComboBox<?> disponibles = comboDeVehiculos(entrada);
            comprobar(disponibles != null && disponibles.getItemCount() == 1
                    && disponibles.getSelectedItem().toString().contains("DOC015"),
                    "Vehiculo listo para solicitar entrada");
            int anteriores = notificaciones;
            comprobar(!sistema.actualizarResidente("NO-EXISTE", "Nadie", "0", false)
                    && notificaciones == anteriores,
                    "Edicion inexistente no notifica ni altera el sistema");
        });
        System.out.println("RESULTADO: " + comprobaciones + " comprobaciones de refresco OK.");
    }

    private void comprobar(boolean condicion, String mensaje) {
        if (!condicion) throw new AssertionError(mensaje);
        comprobaciones++;
        System.out.println("OK " + mensaje);
    }

    private static JComboBox<?> combo(Component componente) {
        JComboBox<?> resultado = buscar(componente, JComboBox.class);
        if (resultado == null) throw new AssertionError("Selector no encontrado");
        return resultado;
    }

    private static <T> T buscar(Component componente, Class<T> tipo) {
        if (tipo.isInstance(componente)) return tipo.cast(componente);
        if (componente instanceof Container) {
            for (Component hijo : ((Container) componente).getComponents()) {
                T resultado = buscar(hijo, tipo);
                if (resultado != null) return resultado;
            }
        }
        return null;
    }

    private static JComboBox<?> comboDeVehiculos(Component componente) {
        if (componente instanceof JComboBox) {
            JComboBox<?> selector = (JComboBox<?>) componente;
            if (selector.getItemCount() > 0 && selector.getItemAt(0) instanceof Vehiculo) {
                return selector;
            }
        }
        if (componente instanceof Container) {
            for (Component hijo : ((Container) componente).getComponents()) {
                JComboBox<?> resultado = comboDeVehiculos(hijo);
                if (resultado != null) return resultado;
            }
        }
        return null;
    }
}
