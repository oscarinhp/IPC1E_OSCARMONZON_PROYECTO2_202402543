import java.awt.Component;
import java.awt.Container;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Window;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import hilos.AdministradorGaritas;
import modelo.EstadoVehiculo;
import modelo.SistemaParqueo;
import modelo.TipoVehiculo;
import modelo.Vehiculo;
import persistencia.GestorArchivos;
import vista.VentanaPrincipal;

/**
 * Capturas reales con Robot de las clases del JAR, sin modificar src ni datos/.
 * Ejecutar en un escritorio visible; no mover ventanas durante la captura.
 * Argumentos: carpeta de datos de lectura, carpeta de salida de imagenes.
 */
public class CapturarPaneles {
    private static final SistemaParqueo sistema = new SistemaParqueo();
    private static final AdministradorGaritas garitas = new AdministradorGaritas(sistema);
    private static VentanaPrincipal ventana;
    private static JTabbedPane pestanas;
    private static Path salida;

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            throw new IllegalArgumentException("Indicar carpeta de datos y salida de imagenes.");
        }
        salida = Path.of(args[1]);
        Files.createDirectories(salida);
        GestorArchivos gestor = new GestorArchivos(args[0]);
        System.out.print(gestor.cargar(sistema));
        if (sistema.listarResidentes().length == 0) {
            throw new IllegalStateException("Se necesitan residentes de ejemplo.");
        }
        try {
            SwingUtilities.invokeAndWait(() -> {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
                // Misma ventana del programa; se omite solo el dialogo de avisos.
                ventana = new VentanaPrincipal(sistema, gestor, "");
                Rectangle pantalla = ventana.getGraphicsConfiguration().getBounds();
                Insets bordes = Toolkit.getDefaultToolkit().getScreenInsets(ventana.getGraphicsConfiguration());
                ventana.setBounds(pantalla.x + bordes.left, pantalla.y + bordes.top,
                        Math.min(1280, pantalla.width - bordes.left - bordes.right),
                        Math.min(780, pantalla.height - bordes.top - bordes.bottom));
                ventana.setAdministradorGaritas(garitas);
                sistema.setObservador(() -> SwingUtilities.invokeLater(ventana::actualizarTodo));
                pestanas = buscar(ventana.getContentPane(), JTabbedPane.class);
                ventana.setVisible(true);
                ventana.toFront();
                ventana.actualizarTodo();
                JTable tabla = buscar(pestanas.getComponentAt(0), JTable.class);
                tabla.setRowSelectionInterval(0, 0);
            });
            capturar("01-residentes.png");
            seleccionar(1);
            capturar("02-vehiculos.png");

            // Datos temporales de demostracion; nunca se llama guardar().
            Vehiculo[] disponibles = sistema.vehiculosListosParaEntrar();
            for (int i = 0; i < Math.min(3, disponibles.length); i++) {
                sistema.solicitarEntrada(disponibles[i].getPlaca());
            }
            for (int i = 0; i < 12; i++) {
                sistema.registrarVisitante("Visita de prueba " + (i + 1), "DOC" + (100 + i),
                        "Toyota", "Corolla 2020", "Blanco", TipoVehiculo.AUTOMOVIL);
                sistema.solicitarEntrada("DOC" + (100 + i));
            }
            garitas.iniciar();
            Thread.sleep(120);
            seleccionar(3);
            SwingUtilities.invokeAndWait(() -> {
                JTextField[] campos = new JTextField[6];
                recolectarCampos(pestanas.getComponentAt(3), campos, 0);
                String[] valores = {"Laura de ejemplo", "P900DOC", "Honda", "Civic 2021", "Azul"};
                for (int i = 0; i < valores.length; i++) {
                    if (campos[i] != null) campos[i].setText(valores[i]);
                }
                ventana.actualizarTodo();
            });
            capturar("04-entrada.png");
            esperarIngresos();
            seleccionar(2);
            capturar("03-parqueo.png");

            Vehiculo[] estacionados = sistema.vehiculosEstacionados();
            for (int i = 0; i < Math.min(8, estacionados.length); i++) {
                sistema.solicitarSalida(estacionados[i].getPlaca());
            }
            Thread.sleep(120);
            seleccionar(4);
            capturar("05-salida.png");
            esperarSalidas();
            seleccionar(5);
            capturar("06-eventos.png");

            // Invoca el boton real de simulacion; captura su dialogo de resumen.
            SwingUtilities.invokeLater(() -> {
                JButton boton = buscarBoton(ventana.getContentPane(), "Simular 10 movimientos");
                if (boton == null) throw new IllegalStateException("No se encontro simulacion.");
                boton.doClick();
            });
            Thread.sleep(650);
            capturar("07-simulacion.png");
            System.out.println("CAPTURAS OK: 7 imagenes reales; sin guardar datos.");
        } finally {
            garitas.detener();
            SwingUtilities.invokeAndWait(() -> {
                for (Window w : Window.getWindows()) {
                    if (w == ventana || esHija(w, ventana)) w.dispose();
                }
            });
        }
    }

    private static boolean esHija(Window w, Window padre) {
        for (Window p = w.getOwner(); p != null; p = p.getOwner()) {
            if (p == padre) return true;
        }
        return false;
    }

    private static void seleccionar(int indice) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            pestanas.setSelectedIndex(indice);
            ventana.actualizarTodo();
        });
    }

    private static void capturar(String nombre) throws Exception {
        Rectangle[] area = new Rectangle[1];
        SwingUtilities.invokeAndWait(() -> area[0] = ventana.getBounds());
        Robot robot = new Robot();
        robot.waitForIdle();
        robot.delay(150);
        ImageIO.write(robot.createScreenCapture(area[0]), "png", salida.resolve(nombre).toFile());
        System.out.println("CAPTURA " + nombre);
    }

    private static void esperarIngresos() throws Exception {
        long limite = System.nanoTime() + 20_000_000_000L;
        while (true) {
            boolean pendiente = false;
            for (Vehiculo v : sistema.visitantesActivos()) {
                pendiente |= v.getEstado() == EstadoVehiculo.EN_COLA_ENTRADA;
            }
            pendiente |= sistema.tamanoColaEntrada() > 0;
            if (!pendiente && sistema.vehiculosEstacionados().length >= 15) return;
            if (System.nanoTime() > limite) throw new AssertionError("Timeout de ingresos.");
            Thread.sleep(50);
        }
    }

    private static void esperarSalidas() throws Exception {
        long limite = System.nanoTime() + 20_000_000_000L;
        while (true) {
            boolean pendiente = sistema.tamanoColaSalida() > 0;
            for (Vehiculo v : sistema.vehiculosEstacionados()) {
                pendiente |= v.getEstado() == EstadoVehiculo.EN_COLA_SALIDA;
            }
            if (!pendiente) return;
            if (System.nanoTime() > limite) throw new AssertionError("Timeout de salidas.");
            Thread.sleep(50);
        }
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

    private static JButton buscarBoton(Component componente, String texto) {
        if (componente instanceof JButton && texto.equals(((JButton) componente).getText())) {
            return (JButton) componente;
        }
        if (componente instanceof Container) {
            for (Component hijo : ((Container) componente).getComponents()) {
                JButton resultado = buscarBoton(hijo, texto);
                if (resultado != null) return resultado;
            }
        }
        return null;
    }

    private static int recolectarCampos(Component c, JTextField[] campos, int indice) {
        if (c instanceof JTextField && indice < campos.length) campos[indice++] = (JTextField) c;
        if (c instanceof Container) {
            for (Component hijo : ((Container) c).getComponents()) {
                indice = recolectarCampos(hijo, campos, indice);
            }
        }
        return indice;
    }
}
