package vista;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import hilos.AdministradorGaritas;
import modelo.SistemaParqueo;
import modelo.TipoEspacio;
import persistencia.GestorArchivos;

/**
 * Ventana principal con las pestañas del sistema. Los paneles se incorporan
 * por etapas: en este momento solo el panel de Residentes es real y los demás
 * muestran un marcador.
 */
public class VentanaPrincipal extends JFrame {

    private final SistemaParqueo sistema;
    private final GestorArchivos gestor;

    private AdministradorGaritas administradorGaritas;
    private PanelResidentes panelResidentes;
    private PanelVehiculos panelVehiculos;
    private PanelParqueo panelParqueo;
    private PanelEntrada panelEntrada;
    private PanelSalida panelSalida;
    private PanelEventos panelEventos;
    private JLabel etiquetaEstado;

    /**
     * Crea la ventana.
     *
     * @param sistema     núcleo compartido
     * @param gestor      persistencia para guardar al cerrar
     * @param avisosCarga avisos de la carga inicial de archivos
     */
    public VentanaPrincipal(SistemaParqueo sistema, GestorArchivos gestor,
                            String avisosCarga) {
        this.sistema = sistema;
        this.gestor = gestor;

        setTitle("Parque Residencial - Sistema de Gestión de Parqueo");
        setSize(1280, 780);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evento) {
                cerrarAplicacion();
            }
        });

        setJMenuBar(crearMenuBar());
        setContentPane(crearContenido());

        if (avisosCarga != null && !avisosCarga.isBlank()) {
            JOptionPane.showMessageDialog(this, avisosCarga,
                    "Carga de datos inicial", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Registra al administrador de garitas para detenerlo al cerrar.
     *
     * @param administrador administrador de garitas
     */
    public void setAdministradorGaritas(AdministradorGaritas administrador) {
        this.administradorGaritas = administrador;
        if (panelEntrada != null) {
            panelEntrada.setAdministradorGaritas(administrador);
        }
        if (panelSalida != null) {
            panelSalida.setAdministradorGaritas(administrador);
        }
    }

    /**
     * El administrador de garitas asignado a la ventana.
     *
     * @return administrador de garitas o {@code null} si no fue asignado
     */
    public AdministradorGaritas getAdministradorGaritas() {
        return administradorGaritas;
    }

    /**
     * Construye las pestañas con el panel de residentes activo.
     *
     * @return panel con todo el contenido de la ventana
     */
    private JPanel crearContenido() {
        JPanel contenido = new JPanel(new BorderLayout());

        contenido.add(crearBarraSimulacion(), BorderLayout.NORTH);

        JTabbedPane pestanas = new JTabbedPane();
        panelResidentes = new PanelResidentes(sistema);
        pestanas.addTab("Residentes", panelResidentes);

        panelVehiculos = new PanelVehiculos(sistema);
        pestanas.addTab("Vehículos", panelVehiculos);
        pestanas.addTab("Parqueo", panelParqueo = new PanelParqueo(sistema));
        pestanas.addTab("Entrada", panelEntrada = new PanelEntrada(sistema));
        pestanas.addTab("Salida", panelSalida = new PanelSalida(sistema));
        pestanas.addTab("Eventos", panelEventos = new PanelEventos(sistema));

        contenido.add(pestanas, BorderLayout.CENTER);

        etiquetaEstado = new JLabel("Sistema listo.");
        etiquetaEstado.setFont(new Font("SansSerif", Font.PLAIN, 12));
        etiquetaEstado.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        contenido.add(etiquetaEstado, BorderLayout.SOUTH);

        return contenido;
    }

    /**
     * Marcador de lugar para paneles aún no implementados.
     *
     * @param texto texto del marcador
     * @return panel centrado
     */
    private JPanel marcador(String texto) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel etiqueta = new JLabel(texto, SwingConstants.CENTER);
        panel.add(etiqueta, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Barra con los botones de simulación del flujo completo: llegada → cola
     * → garita → espacio → salida → eventos.
     *
     * @return barra de simulación
     */
    private JPanel crearBarraSimulacion() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));

        JButton botonVisitante = new JButton("Simular llegada de visitante");
        botonVisitante.setToolTipText("Registra un visitante con datos "
                + "aleatorios y lo manda a la cola de entrada; las garitas "
                + "lo atienden y le asignan un espacio.");
        botonVisitante.addActionListener(e -> ejecutarSimulacion(true));

        JButton botonSalida = new JButton("Simular salida");
        botonSalida.setToolTipText("Pide la salida de un vehículo "
                + "estacionado al azar; su espacio queda libre.");
        botonSalida.addActionListener(e -> ejecutarSimulacion(false));

        JButton botonRafaga = new JButton("Simular 10 movimientos");
        botonRafaga.setToolTipText("Llegadas y salidas mixtas para llenar "
                + "el historial y ver la parte concurrente del sistema.");
        botonRafaga.addActionListener(e -> ejecutarRafaga());

        barra.add(botonVisitante);
        barra.add(botonSalida);
        barra.add(botonRafaga);
        return barra;
    }

    /**
     * Ejecuta una ráfaga de 10 pasos: creamos residentes y visitantes que
     * llegan, intercambiando con salidas cuando hay estacionados.
     */
    private void ejecutarRafaga() {
        StringBuilder resumen = new StringBuilder();
        for (int paso = 0; paso < 10; paso++) {
            try {
                String resultado;
                if (paso % 2 == 0) {
                    resultado = paso % 4 == 0
                            ? Simulador.simularResidente(sistema)
                            : Simulador.simularVisitante(sistema);
                } else {
                    resultado = Simulador.simularSalida(sistema);
                    if (resultado == null) {
                        resultado = Simulador.simularVisitante(sistema);
                    }
                }
                resumen.append(paso + 1).append(". ").append(resultado)
                        .append('\n');
            } catch (IllegalArgumentException e) {
                resumen.append(paso + 1).append(". No se pudo: ")
                        .append(e.getMessage()).append('\n');
            }
        }
        JTextArea areaResumen = new JTextArea(resumen.toString(), 16, 60);
        areaResumen.setEditable(false);
        areaResumen.setFont(Tema.FUENTE_MONO);
        JOptionPane.showMessageDialog(this, new JScrollPane(areaResumen),
                "Ráfaga de simulación", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Ejecuta un paso de simulación mostrando el resultado en la barra de
     * estado o el error en un cuadro de validación.
     *
     * @param llegada {@code true} para simular una llegada; {@code false}
     *                para una salida
     */
    private void ejecutarSimulacion(boolean llegada) {
        try {
            String resultado = llegada
                    ? Simulador.simularVisitante(sistema)
                    : Simulador.simularSalida(sistema);
            if (resultado == null) {
                JOptionPane.showMessageDialog(this,
                        "No hay vehículos estacionados todavía.",
                        "Simulación", JOptionPane.WARNING_MESSAGE);
            } else {
                etiquetaEstado.setText(resultado);
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Validación de la simulación",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Menú superior con salir.
     *
     * @return menú
     */
    private JMenuBar crearMenuBar() {
        JMenuBar menu = new JMenuBar();
        JMenu menuArchivo = new JMenu("Archivo");
        JMenuItem itemSalir = new JMenuItem("Guardar y salir");
        itemSalir.addActionListener(e -> cerrarAplicacion());
        menuArchivo.add(itemSalir);
        menu.add(menuArchivo);

        JMenu menuAyuda = new JMenu("Ayuda");
        JMenuItem itemAcerca = new JMenuItem("Acerca de");
        itemAcerca.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Parque Residencial: Sistema de Gestión de Parqueo\n"
                        + "IPC1E - Segundo Semestre 2026\n"
                        + "Oscar Monzón - Carné 202402543",
                "Acerca de", JOptionPane.INFORMATION_MESSAGE));
        menuAyuda.add(itemAcerca);
        menu.add(menuAyuda);
        return menu;
    }

    /**
     * Refresca todos los paneles. Se registra como observador del sistema y la
     * invocan los hilos de las garitas a través de SwingUtilities.invokeLater.
     */
    public void actualizarTodo() {
        panelResidentes.actualizar();
        panelVehiculos.actualizar();
        panelParqueo.actualizar();
        panelEntrada.actualizar();
        panelSalida.actualizar();
        panelEventos.actualizar();
        actualizarEstado();
    }

    /**
     * Pone en la barra inferior el resumen del sistema.
     */
    private void actualizarEstado() {
        int libresSocio = sistema.contarEspaciosLibres(TipoEspacio.SOCIO);
        int libresGeneral = sistema.contarEspaciosLibres(TipoEspacio.GENERAL);
        int residentes = sistema.listarResidentes().length;
        String estado = "Espacios libres: " + libresSocio + " socios / "
                + libresGeneral + " general | Residentes: " + residentes
                + " | Cola entrada: " + sistema.tamanoColaEntrada()
                + " | Cola salida: " + sistema.tamanoColaSalida();
        etiquetaEstado.setText(estado);
    }

    /**
     * Guarda los datos en los archivos y cierra la aplicación.
     */
    private void cerrarAplicacion() {
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Deseas guardar los datos y salir?",
                "Salir", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (respuesta != JOptionPane.OK_OPTION) {
            return;
        }
        try {
            gestor.guardar(sistema);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "No se guardaron los datos: " + e.getMessage(),
                    "Error al guardar", JOptionPane.ERROR_MESSAGE);
        }
        if (administradorGaritas != null) {
            administradorGaritas.detener();
        }
        dispose();
        System.exit(0);
    }
}
