package vista;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import hilos.AdministradorGaritas;
import hilos.GaritaEntrada;
import modelo.SistemaParqueo;
import modelo.TipoVehiculo;
import modelo.Vehiculo;

/**
 * Panel de la puerta de entrada: registra visitantes, solicita el ingreso de
 * vehículos de residentes y muestra la cola compartida que atienden los 3
 * hilos de garita de entrada.
 */
public class PanelEntrada extends JPanel {

    private final SistemaParqueo sistema;

    private final JTextField campoNombreVisitante = new JTextField(14);
    private final JTextField campoPlaca = new JTextField(10);
    private final JTextField campoMarca = new JTextField(10);
    private final JTextField campoModelo = new JTextField(10);
    private final JTextField campoColor = new JTextField(10);
    private final JComboBox<TipoVehiculo> comboTipo =
            new JComboBox<>(TipoVehiculo.values());

    private final JButton botonRegistrarVisitante =
            new JButton("Registrar visitante y pedir entrada");
    private final JButton botonEntradaResidente =
            new JButton("Solicitar entrada");
    private final JComboBox<Vehiculo> comboListosParaEntrar = new JComboBox<>();

    private final VehiculosTableModel modeloCola = new VehiculosTableModel();
    private final JTable tablaCola = new JTable(modeloCola);

    private final JLabel etiquetaGaritas = new JLabel(" ");

    private AdministradorGaritas administrador;

    /**
     * Crea el panel.
     *
     * @param sistema núcleo del sistema
     */
    public PanelEntrada(SistemaParqueo sistema) {
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

        botonRegistrarVisitante.addActionListener(e -> registrarVisitante());
        botonEntradaResidente.addActionListener(e -> solicitarEntradaResidente());

        actualizar();
    }

    /**
     * Recibe el administrador de garitas para mostrar el estado de las
     * garitas de entrada.
     *
     * @param administrador administrador ya iniciado
     */
    public void setAdministradorGaritas(AdministradorGaritas administrador) {
        this.administrador = administrador;
        actualizar();
    }

    /**
     * Formulario del visitante y botón para el residente.
     *
     * @return panel izquierdo
     */
    private JPanel crearPanelIzquierdo() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Tema.COLOR_PANEL_LATERAL);
        panel.setBorder(BorderFactory.createTitledBorder("Entrada"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.insets = new Insets(4, 6, 2, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridy++;
        panel.add(new JLabel("Visitante - nombre"), gbc);
        gbc.gridy++;
        panel.add(campoNombreVisitante, gbc);
        gbc.gridy++;
        panel.add(new JLabel("Visitante - placa"), gbc);
        gbc.gridy++;
        panel.add(campoPlaca, gbc);
        gbc.gridy++;
        panel.add(new JLabel("Marca"), gbc);
        gbc.gridy++;
        panel.add(campoMarca, gbc);
        gbc.gridy++;
        panel.add(new JLabel("Modelo"), gbc);
        gbc.gridy++;
        panel.add(campoModelo, gbc);
        gbc.gridy++;
        panel.add(new JLabel("Color"), gbc);
        gbc.gridy++;
        panel.add(campoColor, gbc);
        gbc.gridy++;
        panel.add(new JLabel("Tipo"), gbc);
        gbc.gridy++;
        panel.add(comboTipo, gbc);

        gbc.gridy++;
        panel.add(botonRegistrarVisitante, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(12, 6, 2, 6);
        panel.add(new JLabel("Vehículo de residente (listo para entrar)"), gbc);
        gbc.insets = new Insets(4, 6, 2, 6);
        gbc.gridy++;
        panel.add(comboListosParaEntrar, gbc);
        gbc.gridy++;
        panel.add(botonEntradaResidente, gbc);

        return panel;
    }

    /**
     * Refresca el combo, la cola y el estado de las garitas.
     */
    public final void actualizar() {
        Vehiculo seleccionado = (Vehiculo) comboListosParaEntrar.getSelectedItem();
        String placaSeleccionada = seleccionado == null ? null : seleccionado.getPlaca();

        comboListosParaEntrar.removeAllItems();
        Vehiculo[] disponibles = sistema.vehiculosListosParaEntrar();
        int seleccion = -1;
        for (int i = 0; i < disponibles.length; i++) {
            comboListosParaEntrar.addItem(disponibles[i]);
            if (disponibles[i].getPlaca().equals(placaSeleccionada)) {
                seleccion = i;
            }
        }
        if (seleccion >= 0) {
            comboListosParaEntrar.setSelectedIndex(seleccion);
        }

        modeloCola.setDatos(sistema.snapshotsColaEntrada());
        actualizarGaritas();
    }

    /**
     * Muestra el estado de las 3 garitas de entrada.
     */
    private void actualizarGaritas() {
        if (administrador == null) {
            etiquetaGaritas.setText("Garitas de entrada aún no iniciadas.");
            return;
        }
        StringBuilder texto = new StringBuilder("Garitas: ");
        GaritaEntrada[] garitas = administrador.getGaritasEntrada();
        for (int i = 0; i < garitas.length; i++) {
            if (i > 0) {
                texto.append(" | ");
            }
            GaritaEntrada garita = garitas[i];
            if (garita != null) {
                texto.append(garita.getNombre()).append(": ");
                texto.append(garita.estaAtendiendo()
                        ? "atendiendo " + garita.getVehiculoAtendido()
                        : "libre");
            } else {
                texto.append("Entrada-").append(i + 1).append(": pendiente");
            }
        }
        etiquetaGaritas.setText(texto.toString());
    }

    /**
     * Texto corto para una garita ocupada.
     *
     * @param placa placa en atención
     * @return texto
     */
    private String atendiendo(String placa) {
        return "atendiendo " + placa;
    }

    /**
     * Registra al visitante del formulario y lo manda a la cola de entrada.
     */
    private void registrarVisitante() {
        try {
            Vehiculo visitante = sistema.registrarVisitante(
                    campoNombreVisitante.getText().trim(),
                    campoPlaca.getText().trim(),
                    campoMarca.getText().trim(),
                    campoModelo.getText().trim(),
                    campoColor.getText().trim(),
                    (TipoVehiculo) comboTipo.getSelectedItem());
            sistema.solicitarEntrada(visitante.getPlaca());
            JOptionPane.showMessageDialog(this,
                    "Visitante " + visitante.getPlaca()
                            + " en cola de entrada.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            campoNombreVisitante.setText("");
            campoPlaca.setText("");
            campoMarca.setText("");
            campoModelo.setText("");
            campoColor.setText("");
            comboTipo.setSelectedIndex(0);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "No se pudo registrar", JOptionPane.WARNING_MESSAGE);
        }
        actualizar();
    }

    /**
     * Pone en la cola de entrada al vehículo de residente elegido.
     */
    private void solicitarEntradaResidente() {
        Vehiculo seleccionado = (Vehiculo) comboListosParaEntrar.getSelectedItem();
        if (seleccionado == null) {
            JOptionPane.showMessageDialog(this,
                    "No hay vehículos listos para entrar.",
                    "Sin vehículos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            sistema.solicitarEntrada(seleccionado.getPlaca());
            JOptionPane.showMessageDialog(this,
                    "Vehículo " + seleccionado.getPlaca()
                            + " en cola de entrada.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "No se pudo solicitar", JOptionPane.WARNING_MESSAGE);
        }
        actualizar();
    }
}
