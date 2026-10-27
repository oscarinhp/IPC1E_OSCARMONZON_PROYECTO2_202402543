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
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import modelo.Residente;
import modelo.SistemaParqueo;
import modelo.TipoVehiculo;
import modelo.Vehiculo;

/**
 * Panel para registrar y eliminar los vehículos de un residente.
 *
 * <p>Se elige un residente y sobre su lista simplemente enlazada se agregan o
 * eliminan vehículos. La regla de negocio de máximo 3 vehículos se valida en
 * {@code Residente.agregarVehiculo}; aquí se muestra el contador.</p>
 */
public class PanelVehiculos extends JPanel {

    private final SistemaParqueo sistema;

    private final JComboBox<Residente> comboResidentes = new JComboBox<>();

    private final JTextField campoPlaca = new JTextField(10);
    private final JTextField campoMarca = new JTextField(10);
    private final JTextField campoModelo = new JTextField(10);
    private final JTextField campoColor = new JTextField(10);
    private final JComboBox<TipoVehiculo> comboTipo =
            new JComboBox<>(TipoVehiculo.values());

    private final JButton botonAgregar = new JButton("Agregar vehículo");
    private final JButton botonEliminar = new JButton("Eliminar vehículo");
    private final JButton botonLimpiar = new JButton("Limpiar formulario");

    private final JLabel etiquetaContador = new JLabel("Vehículos: 0/3");

    private final VehiculosTableModel modeloTabla = new VehiculosTableModel();
    private final JTable tabla = new JTable(modeloTabla);

    /**
     * Crea el panel.
     *
     * @param sistema núcleo del sistema
     */
    public PanelVehiculos(SistemaParqueo sistema) {
        this.sistema = sistema;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(crearPanelIzquierdo(), BorderLayout.WEST);

        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setPreferredSize(new Dimension(620, 260));
        JPanel derecha = new JPanel(new BorderLayout(8, 8));
        derecha.add(scrollTabla, BorderLayout.NORTH);
        derecha.add(etiquetaContador, BorderLayout.SOUTH);
        add(derecha, BorderLayout.CENTER);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent evento) {
                if (!evento.getValueIsAdjusting()) {
                    cargarFormularioDesdeRow();
                }
            }
        });

        comboResidentes.addActionListener(e -> cargarVehiculos());
        botonAgregar.addActionListener(e -> agregar());
        botonEliminar.addActionListener(e -> eliminar());
        botonLimpiar.addActionListener(e -> limpiarFormulario());

        actualizar();
    }

    /**
     * Carga el residente seleccionado y refresca su lista de vehículos.
     */
    public final void actualizar() {
        String idSeleccionado = idSeleccionado();
        Residente[] residentes = sistema.listarResidentes();

        comboResidentes.removeAllItems();
        for (Residente residente : residentes) {
            comboResidentes.addItem(residente);
        }

        if (idSeleccionado != null) {
            for (int i = 0; i < comboResidentes.getItemCount(); i++) {
                if (comboResidentes.getItemAt(i).getId().equals(idSeleccionado)) {
                    comboResidentes.setSelectedIndex(i);
                    break;
                }
            }
        }

        cargarVehiculos();
    }

    /**
     * ID del residente seleccionado en el combo.
     *
     * @return el id o {@code null} si no hay residente
     */
    private String idSeleccionado() {
        Object item = comboResidentes.getSelectedItem();
        if (item instanceof Residente) {
            return ((Residente) item).getId();
        }
        return null;
    }

    /**
     * Formulario a la izquierda.
     *
     * @return panel con los campos del vehículo
     */
    private JPanel crearPanelIzquierdo() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Tema.COLOR_PANEL_LATERAL);
        panel.setBorder(BorderFactory.createTitledBorder("Vehículo"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.insets = new Insets(4, 6, 2, 6);

        gbc.gridy++;
        panel.add(new JLabel("Residente dueño"), gbc);
        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(comboResidentes, gbc);

        gbc.gridy++;
        panel.add(new JLabel("Placa"), gbc);
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

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
        botones.add(botonAgregar);
        botones.add(botonEliminar);
        botones.add(botonLimpiar);
        gbc.gridy++;
        gbc.gridwidth = 1;
        panel.add(botones, gbc);

        return panel;
    }

    /**
     * Vuelve a leer los vehículos del residente seleccionado.
     */
    private void cargarVehiculos() {
        String id = idSeleccionado();
        if (id == null) {
            modeloTabla.setDatos(new Vehiculo[0]);
            etiquetaContador.setText("Vehículos: 0/3");
            return;
        }
        Vehiculo[] vehiculos = sistema.vehiculosDeResidente(id);
        modeloTabla.setDatos(vehiculos);
        etiquetaContador.setText("Vehículos: " + vehiculos.length + "/3");
    }

    /**
     * Carga los datos del vehículo seleccionado en la tabla.
     */
    private void cargarFormularioDesdeRow() {
        Vehiculo vehiculo = modeloTabla.en(tabla.getSelectedRow());
        if (vehiculo == null) {
            return;
        }
        campoPlaca.setText(vehiculo.getPlaca());
        campoMarca.setText(vehiculo.getMarca());
        campoModelo.setText(vehiculo.getModelo());
        campoColor.setText(vehiculo.getColor());
        comboTipo.setSelectedItem(vehiculo.getTipo());
    }

    /**
     * Limpia los campos del formulario.
     */
    private void limpiarFormulario() {
        campoPlaca.setText("");
        campoMarca.setText("");
        campoModelo.setText("");
        campoColor.setText("");
        comboTipo.setSelectedIndex(0);
        tabla.clearSelection();
        campoPlaca.requestFocus();
    }

    /**
     * Registra el vehículo del formulario para el residente seleccionado.
     */
    private void agregar() {
        String idResidente = idSeleccionado();
        if (idResidente == null) {
            JOptionPane.showMessageDialog(this,
                    "Primero registra un residente.",
                    "Sin residente", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Vehiculo vehiculo = sistema.registrarVehiculo(idResidente,
                    campoPlaca.getText().trim(),
                    campoMarca.getText().trim(),
                    campoModelo.getText().trim(),
                    campoColor.getText().trim(),
                    (TipoVehiculo) comboTipo.getSelectedItem());
            JOptionPane.showMessageDialog(this,
                    "Vehículo " + vehiculo.getPlaca() + " registrado.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarVehiculos();
            limpiarFormulario();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "No se pudo registrar", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Elimina el vehículo seleccionado en la tabla.
     */
    private void eliminar() {
        Vehiculo vehiculo = modeloTabla.en(tabla.getSelectedRow());
        if (vehiculo == null) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona un vehículo en la tabla.",
                    "Nada seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String idResidente = idSeleccionado();
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Eliminar el vehículo " + vehiculo.getPlaca() + "?",
                "Confirmar", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (respuesta != JOptionPane.OK_OPTION) {
            return;
        }
        try {
            sistema.eliminarVehiculo(idResidente, vehiculo.getPlaca());
            cargarVehiculos();
            limpiarFormulario();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "No se pudo eliminar", JOptionPane.WARNING_MESSAGE);
        }
    }
}
