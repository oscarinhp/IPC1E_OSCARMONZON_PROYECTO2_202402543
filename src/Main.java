import hilos.AdministradorGaritas;
import modelo.SistemaParqueo;
import persistencia.GestorArchivos;
import vista.VentanaPrincipal;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Punto de entrada del Sistema de Gestión de Parqueo del Parque Residencial.
 *
 * <p>Al iniciar se reconstruyen las estructuras desde los archivos de datos y
 * se conecta la interfaz con el sistema; después arrancan los hilos de las 5
 * garitas.</p>
 *
 * @author Oscar Monzón - Carné 202402543
 */
public class Main {

    /** Carpeta donde viven residentes.txt y vehiculos.txt. */
    public static final String CARPETA_DATOS = "datos";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Si no hay look and feel del sistema se usa el de Java.
            }

            SistemaParqueo sistema = new SistemaParqueo();
            GestorArchivos gestor = new GestorArchivos(CARPETA_DATOS);
            String avisos;
            try {
                avisos = gestor.cargar(sistema);
            } catch (java.io.IOException e) {
                avisos = "No se pudieron leer los archivos de datos: "
                        + e.getMessage();
            }

            VentanaPrincipal ventana = new VentanaPrincipal(sistema, gestor, avisos);
            // Los hilos de las garitas avisan por medio de este observador; el
            // refresco de Swing se hace en el hilo de eventos (EDT).
            sistema.setObservador(() -> SwingUtilities.invokeLater(ventana::actualizarTodo));
            ventana.setAdministradorGaritas(new AdministradorGaritas(sistema));
            ventana.setVisible(true);

            if (ventana.getAdministradorGaritas() != null) {
                ventana.getAdministradorGaritas().iniciar();
            }
            ventana.actualizarTodo();
        });
    }
}
