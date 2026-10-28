package vista;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import hilos.AdministradorGaritas;
import hilos.GaritaSalida;
import modelo.SistemaParqueo;
import modelo.Vehiculo;

/**
 * Panel de la puerta de salida: solicita la salida de vehículos estacionados,
 * muestra la cola que atienden los 2 hilos de garita de salida y lista a los
 * visitantes todavía dentro del residencial.
 */
public class PanelSalida extends JPanel {

    private final SistemaParqueo sistema;

    private final JComboBox<Vehiculo> comboEstacionados = new JComboBox<>();
    private final JButton botonSolicitarSalida = new JButton("Solicitar salida");

    private final VehiculosTableModel modeloCola = new VehiculosTableModel();
    private final JTable tablaCola = new JTable(modeloCola);

    private final JTextArea areaVisitantes = new JTextArea(6, 30);
    private final JLabel etiquetaGaritas = new JLabel(" ");

    private AdministradorGaritas administrador;

    /**
     * Crea el panel.
     *
     * @param sistema núcleo del sistema
     */
    public PanelSalida(SistemaParqueo sistema) {
        this.sistema = sistema;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(crearPanelIzquierdo(), BorderLayout.WEST);

        JPanel derecha = new JPanel(new BorderLayout(8, 8));
        JScrollPane scroll = new JScrollPane(tablaCola);
        scroll.setPreferredSize(new Dimension(620, 220));
        derecha.add(scroll, BorderLayout.CENTER);
        derecha.add(etiquetaGaritas, BorderLayout.SOUTH);
        add(derecha, BorderLayout.CENTER);

        areaVisitantes.setEditable(false);
        areaVisitantes.setFont(Tema.FUENTE_MONO);

        botonSolicitarSalida.addActionListener(e -> solicitarSalida());

        actualizar();
    }

    /**
     * Recibe el administrador de garitas para mostrar el estado de las
     * garitas de salida.
     *
     * @param administrador administrador ya iniciado
     */
    public void setAdministradorGaritas(AdministradorGaritas administrador) {
        this.administrador = administrador;
        actualizar();
    }

    /**
     * Formulario con el combo de estacionados.
     *
     * @return panel izquierdo
     */
    private JPanel crearPanelIzquierdo() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Tema.COLOR_PANEL_LATERAL);
        panel.setBorder(BorderFactory.createTitledBorder("Salida"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.insets = new Insets(4, 6, 2, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridy++;
        panel.add(new JLabel("Vehículo estacionado"), gbc);
        gbc.gridy++;
        panel.add(comboEstacionados, gbc);
        gbc.gridy++;
        panel.add(botonSolicitarSalida, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(12, 6, 2, 6);
        panel.add(new JLabel("Visitantes aún dentro del residencial"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(4, 6, 2, 6);
        panel.add(new JScrollPane(areaVisitantes), gbc);

        return panel;
    }

    /**
     * Refresca el combo de estacionados, la cola, el estado de garitas y el
     * listado de visitantes.
     */
    public final void actualizar() {
        Vehiculo seleccionado = (Vehiculo) comboEstacionados.getSelectedItem();
        String placaSeleccionada = seleccionado == null ? null : seleccionado.getPlaca();

        comboEstacionados.removeAllItems();
        Vehiculo[] estacionados = sistema.vehiculosEstacionados();
        int seleccion = -1;
        for (int i = 0; i < estacionados.length; i++) {
            comboEstacionados.addItem(estacionados[i]);
            if (estacionados[i].getPlaca().equals(placaSeleccionada)) {
                seleccion = i;
            }
        }
        if (seleccion >= 0) {
            comboEstacionados.setSelectedIndex(seleccion);
        }

        modeloCola.setDatos(sistema.snapshotsColaSalida());
        actualizarGaritas();

        StringBuilder texto = new StringBuilder();
        Vehiculo[] visitantes = sistema.visitantesActivos();
        if (visitantes.length == 0) {
            texto.append("(no hay visitantes dentro)");
        } else {
            for (Vehiculo visitante : visitantes) {
                texto.append('•').append(' ').append(visitante.toString())
                        .append(" — ").append(visitante.getNombreVisitante())
                        .append('\n');
            }
        }
        areaVisitantes.setText(texto.toString());
    }

    /**
     * Muestra el estado de las 2 garitas de salida.
     */
    private void actualizarGaritas() {
        if (administrador == null) {
            etiquetaGaritas.setText("Garitas de salida aún no iniciadas.");
            return;
        }
        StringBuilder texto = new StringBuilder("Garitas: ");
        GaritaSalida[] garitas = administrador.getGaritasSalida();
        for (int i = 0; i < garitas.length; i++) {
            if (i > 0) {
                texto.append(" | ");
            }
            GaritaSalida garita = garitas[i];
            texto.append(garita.getNombre()).append(": ");
            texto.append(garita.estaAtendiendo()
                    ? "atendiendo " + garita.getVehiculoAtendido()
                    : "libre");
        }
        etiquetaGaritas.setText(texto.toString());
    }

    /**
     * Pide la salida del vehículo estacionado elegido.
     */
    private void solicitarSalida() {
        Vehiculo seleccionado = (Vehiculo) comboEstacionados.getSelectedItem();
        if (seleccionado == null) {
            JOptionPane.showMessageDialog(this,
                    "No hay vehículos estacionados.",
                    "Sin vehículos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            sistema.solicitarSalida(seleccionado.getPlaca());
            JOptionPane.showMessageDialog(this,
                    "Vehículo " + seleccionado.getPlaca()
                            + " en cola de salida.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "No se pudo solicitar", JOptionPane.WARNING_MESSAGE);
        }
        actualizar();
    }
}
