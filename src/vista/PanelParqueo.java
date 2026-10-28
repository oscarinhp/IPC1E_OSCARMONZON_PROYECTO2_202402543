package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import modelo.EspacioParqueo;
import modelo.Parqueo;
import modelo.SistemaParqueo;
import modelo.TipoEspacio;
import modelo.Vehiculo;

/**
 * Panel con el mapa visual del parqueo: 150 espacios dibujados como una
 * retícula donde cada botonera refleja el estado del espacio en tiempo real.
 *
 * <p>Los colores provienen del tema: azul para la zona de socios (A-C),
 * verde para la general (E-I), naranja si lo ocupa un residente y rosado si
 * lo ocupa un visitante. Cada espacio también mantiene enlaces arriba/abajo,
 * que se prueban desde el panel con la búsqueda por ID.</p>
 */
public class PanelParqueo extends JPanel {

    /** Tamaño fijo de cada botonera de espacio. */
    private static final Dimension TAMANO_CELDA = new Dimension(46, 40);

    private final SistemaParqueo sistema;
    private final JPanel panelRetícula = new JPanel();
    private final JTextField campoBusqueda = new JTextField(8);
    private final JLabel etiquetaResumen = new JLabel(" ");

    /**
     * Crea el panel.
     *
     * @param sistema núcleo del sistema
     */
    public PanelParqueo(SistemaParqueo sistema) {
        this.sistema = sistema;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(crearBarraSuperior(), BorderLayout.NORTH);

        panelRetícula.setLayout(new BoxLayout(panelRetícula, BoxLayout.Y_AXIS));
        JScrollPane scroll = new JScrollPane(panelRetícula);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        add(scroll, BorderLayout.CENTER);

        add(crearLeyenda(), BorderLayout.SOUTH);

        actualizar();
    }

    /**
     * Barra con la búsqueda de espacio por ID y el resumen de disponibilidad.
     *
     * @return panel superior
     */
    private JPanel crearBarraSuperior() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));

        barra.add(new JLabel("Espacio (ej. B7):"));
        barra.add(campoBusqueda);
        JButton botonBuscar = new JButton("Buscar espacio");
        botonBuscar.addActionListener(e -> buscarEspacio());
        barra.add(botonBuscar);
        barra.add(etiquetaResumen);
        return barra;
    }

    /**
     * Leyenda de colores del mapa.
     *
     * @return panel inferior
     */
    private JPanel crearLeyenda() {
        JPanel leyenda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        leyenda.add(crearMuestra(Tema.COLOR_SOCIO, "Socio libre (A-C)"));
        leyenda.add(crearMuestra(Tema.COLOR_GENERAL, "General libre (E-I)"));
        leyenda.add(crearMuestra(Tema.COLOR_OCUPADO, "Ocupado por residente"));
        leyenda.add(crearMuestra(Tema.COLOR_OCUPADO_VISITANTE,
                "Ocupado por visitante"));
        return leyenda;
    }

    /**
     * Cuadrito de color con texto, para la leyenda.
     *
     * @param color color de la muestra
     * @param texto descripción
     * @return componente de la leyenda
     */
    private Component crearMuestra(Color color, String texto) {
        JPanel muestra = new JPanel();
        muestra.setPreferredSize(new Dimension(14, 14));
        muestra.setBackground(color);
        muestra.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JPanel contenedor = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        contenedor.add(muestra);
        contenedor.add(new JLabel(texto));
        return contenedor;
    }

    /**
     * Redibuja el mapa completo leyendo el estado actual del sistema.
     */
    public final void actualizar() {
        panelRetícula.removeAll();

        EspacioParqueo[] espacios = sistema.getParqueo().espaciosEnOrden();
        char letraActual = 0;
        JPanel filaActual = null;

        for (EspacioParqueo espacio : espacios) {
            if (espacio.getFila() != letraActual) {
                letraActual = espacio.getFila();
                filaActual = crearFila(letraActual, espacio.getTipo());
                panelRetícula.add(filaActual);
            }
            filaActual.add(crearCelda(espacio));
        }

        actualizarResumen();
        panelRetícula.revalidate();
        panelRetícula.repaint();
    }

    /**
     * Crea el contenedor horizontal de una fila con su título.
     *
     * @param letra letra de la fila
     * @param tipo  zona a la que pertenece
     * @return panel de la fila
     */
    private JPanel crearFila(char letra, TipoEspacio tipo) {
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 3));
        JLabel titulo = new JLabel(letra + ":");
        titulo.setPreferredSize(new Dimension(24, TAMANO_CELDA.height));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setVerticalAlignment(SwingConstants.CENTER);
        titulo.setFont(Tema.FUENTE_ESTADO);
        fila.add(titulo);
        return fila;
    }

    /**
     * Crea la botonera de un espacio con su color y tooltip.
     *
     * @param espacio espacio del sistema
     * @return la botonera
     */
    private JButton crearCelda(EspacioParqueo espacio) {
        JButton celda = new JButton(String.valueOf(espacio.getNumero()));
        celda.setPreferredSize(TAMANO_CELDA);
        celda.setMargin(new java.awt.Insets(1, 1, 1, 1));
        celda.setFont(Tema.FUENTE_MONO);
        celda.putClientProperty("espacio", espacio);

        if (espacio.estaOcupado()) {
            Vehiculo vehiculo = espacio.getVehiculo();
            celda.setBackground(vehiculo.isVisitante()
                    ? Tema.COLOR_OCUPADO_VISITANTE
                    : Tema.COLOR_OCUPADO);
            celda.setToolTipText("<html>" + espacio.getId()
                    + "<br>" + vehiculo.toString()
                    .replace("<", "&lt;")
                    + "<br>Propietario: " + vehiculo.getPropietario()
                    + "</html>");
        } else {
            celda.setBackground(espacio.getTipo() == TipoEspacio.SOCIO
                    ? Tema.COLOR_SOCIO
                    : Tema.COLOR_GENERAL);
            celda.setToolTipText(espacio.getId() + " - Libre ("
                    + espacio.getTipo().getEtiqueta() + ")");
        }
        celda.addActionListener(evento ->
                mostrarDetalles(espacio));
        return celda;
    }

    /**
     * Muestra en un cuadro de diálogo el detalle de un espacio, incluyendo
     * sus enlaces arriba/abajo (estructura matricial).
     *
     * @param espacio espacio a describir
     */
    private void mostrarDetalles(EspacioParqueo espacio) {
        StringBuilder texto = new StringBuilder();
        texto.append("Espacio: ").append(espacio.getId()).append('\n');
        texto.append("Zona: ").append(espacio.getTipo().getEtiqueta()).append('\n');
        if (espacio.estaOcupado()) {
            Vehiculo vehiculo = espacio.getVehiculo();
            texto.append("Estado: Ocupado\n");
            texto.append("Vehículo: ").append(vehiculo.toString()).append('\n');
            texto.append("Color: ").append(vehiculo.getColor()).append('\n');
            texto.append("Tipo: ").append(vehiculo.getTipo().getEtiqueta())
                    .append('\n');
            texto.append("Propietario: ").append(vehiculo.getPropietario())
                    .append('\n');
        } else {
            texto.append("Estado: Libre\n");
        }
        texto.append("De arriba: ")
                .append(espacio.getArriba() == null
                        ? "(no tiene)"
                        : espacio.getArriba().getId())
                .append('\n');
        texto.append("De abajo: ")
                .append(espacio.getAbajo() == null
                        ? "(no tiene)"
                        : espacio.getAbajo().getId())
                .append('\n');
        JOptionPane.showMessageDialog(this, texto.toString(),
                "Espacio " + espacio.getId(), JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Busca un espacio por su ID (por ejemplo {@code D5} o {@code A12}) y
     * muestra sus detalles.
     */
    private void buscarEspacio() {
        String texto = campoBusqueda.getText().trim().toUpperCase();
        if (texto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Escribe un ID de espacio.",
                    "Búsqueda", JOptionPane.WARNING_MESSAGE);
            return;
        }
        EspacioParqueo[] espacios = sistema.getParqueo().espaciosEnOrden();
        for (EspacioParqueo espacio : espacios) {
            if (espacio.getId().equalsIgnoreCase(texto)) {
                mostrarDetalles(espacio);
                return;
            }
        }
        JOptionPane.showMessageDialog(this,
                "No existe el espacio " + texto + ".",
                "Búsqueda", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Actualiza la etiqueta con el resumen de espacios.
     */
    private void actualizarResumen() {
        Parqueo parqueo = sistema.getParqueo();
        int libresSocio = parqueo.contarLibres(TipoEspacio.SOCIO);
        int totalSocio = parqueo.contarTotales(TipoEspacio.SOCIO);
        int libresGeneral = parqueo.contarLibres(TipoEspacio.GENERAL);
        int totalGeneral = parqueo.contarTotales(TipoEspacio.GENERAL);
        int ocupados = parqueo.contarOcupados(TipoEspacio.SOCIO)
                + parqueo.contarOcupados(TipoEspacio.GENERAL);
        etiquetaResumen.setText("Socios: " + libresSocio + "/" + totalSocio
                + " libres | General: " + libresGeneral + "/" + totalGeneral
                + " libres | Ocupados: " + ocupados);
    }
}
