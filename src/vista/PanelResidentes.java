package vista;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import modelo.Residente;
import modelo.SistemaParqueo;
import modelo.Vehiculo;

/**
 * Panel para registrar, buscar, modificar, eliminar y recorrer residentes.
 *
 * <p>La tabla se alimenta de la {@code ListaDoble} de residentes; los botones
 * de recorrido permiten mostrar la lista de inicio a fin o de fin a inicio
 * para demostrar los enlaces en ambas direcciones.</p>
 */
public class PanelResidentes extends JPanel {

    private final SistemaParqueo sistema;

    private final JTextField campoId = new JTextField(8);
    private final JTextField campoNombre = new JTextField(16);
    private final JTextField campoCasa = new JTextField(8);
    private final JCheckBox casillaSocio = new JCheckBox("Socio");
    private final JTextField campoBusqueda = new JTextField(12);

    private final JButton botonGuardar = new JButton("Guardar");
    private final JButton botonEliminar = new JButton("Eliminar");
    private final JButton botonBuscar = new JButton("Buscar");
    private final JButton botonNuevo = new JButton("Nuevo");
    private final JButton botonVerTodos = new JButton("Ver todos");
    private final JButton botonRecorridoNormal = new JButton("Recorrer inicio → fin");
    private final JButton botonRecorridoInverso = new JButton("Recorrer fin → inicio");

    private final ResidentesTableModel modeloTabla = new ResidentesTableModel();
    private final JTable tabla = new JTable(modeloTabla);
    private final JTextArea areaVehiculos = new JTextArea(8, 24);

    private boolean ordenInverso = false;
    private boolean editando = false;

    /**
     * Crea el panel.
     *
     * @param sistema núcleo del sistema
     */
    public PanelResidentes(SistemaParqueo sistema) {
        this.sistema = sistema;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(crearPanelIzquierdo(), BorderLayout.WEST);

        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setPreferredSize(new Dimension(560, 260));
        JPanel derecha = new JPanel(new BorderLayout(8, 8));
        derecha.add(scrollTabla, BorderLayout.NORTH);
        derecha.add(crearPanelVehiculos(), BorderLayout.CENTER);
        add(derecha, BorderLayout.CENTER);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent evento) {
                if (!evento.getValueIsAdjusting()) {
                    cargarDesdeTabla();
                }
            }
        });

        botonGuardar.addActionListener(e -> guardar());
        botonEliminar.addActionListener(e -> eliminar());
        botonBuscar.addActionListener(e -> buscar());
        botonNuevo.addActionListener(e -> modoNuevo());
        botonVerTodos.addActionListener(e -> {
            campoBusqueda.setText("");
            actualizarDatos();
        });
        botonRecorridoNormal.addActionListener(e -> {
            ordenInverso = false;
            actualizarDatos();
        });
        botonRecorridoInverso.addActionListener(e -> {
            ordenInverso = true;
            actualizarDatos();
        });

        actualizarDatos();
    }

    /**
     * Formulario a la izquierda con los datos del residente.
     *
     * @return panel con el formulario
     */
    private JPanel crearPanelIzquierdo() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Tema.COLOR_PANEL_LATERAL);
        panel.setBorder(BorderFactory.createTitledBorder("Residente"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.insets = new Insets(4, 6, 2, 6);

        JLabel etiquetaId = new JLabel("ID (automático si se deja vacío)");
        gbc.gridy++;
        panel.add(etiquetaId, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy++;
        panel.add(campoId, gbc);

        gbc.gridy++;
        panel.add(new JLabel("Nombre"), gbc);
        gbc.gridy++;
        panel.add(campoNombre, gbc);

        gbc.gridy++;
        panel.add(new JLabel("Casa / Lote"), gbc);
        gbc.gridy++;
        panel.add(campoCasa, gbc);

        gbc.gridy++;
        panel.add(casillaSocio, gbc);

        JPanel botones = new JPanel(new GridBagLayout());
        GridBagConstraints gbcB = new GridBagConstraints();
        gbcB.insets = new Insets(3, 3, 3, 3);
        gbcB.fill = GridBagConstraints.HORIZONTAL;
        gbcB.gridy = 0;
        gbcB.gridx = 0;
        botones.add(botonGuardar, gbcB);
        gbcB.gridx = 1;
        botones.add(botonEliminar, gbcB);
        gbcB.gridy = 1;
        gbcB.gridx = 0;
        botones.add(botonBuscar, gbcB);
        gbcB.gridx = 1;
        botones.add(botonNuevo, gbcB);
        gbcB.gridy = 2;
        gbcB.gridx = 0;
        botones.add(botonVerTodos, gbcB);
        gbcB.gridx = 1;
        botones.add(campoBusqueda, gbcB);
        gbcB.gridy = 3;
        gbcB.gridx = 0;
        gbcB.gridwidth = 2;
        botones.add(botonRecorridoNormal, gbcB);
        gbcB.gridy = 4;
        botones.add(botonRecorridoInverso, gbcB);

        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(botones, gbc);

        return panel;
    }

    /**
     * Área con los vehículos del residente seleccionado.
     *
     * @return panel con la lista de vehículos
     */
    private JPanel crearPanelVehiculos() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Vehículos del residente seleccionado"));
        areaVehiculos.setEditable(false);
        areaVehiculos.setFont(Tema.FUENTE_MONO);
        panel.add(new JScrollPane(areaVehiculos), BorderLayout.CENTER);
        return panel;
    }

    /**
     * Refresca la tabla con los residentes del sistema, respetando la búsqueda
     * y el orden de recorrido elegidos.
     */
    public final void actualizarDatos() {
        Residente[] residentes;
        String filtro = campoBusqueda.getText().trim();
        if (!filtro.isEmpty()) {
            if (filtro.toUpperCase().startsWith("R") && filtro.length() <= 6) {
                Residente porId = sistema.buscarResidentePorId(filtro.toUpperCase());
                if (porId != null) {
                    residentes = new Residente[]{porId};
                } else {
                    residentes = sistema.buscarResidentesPorNombre(filtro);
                }
            } else {
                residentes = sistema.buscarResidentesPorNombre(filtro);
            }
        } else if (ordenInverso) {
            residentes = sistema.listarResidentesInverso();
        } else {
            residentes = sistema.listarResidentes();
        }
        modeloTabla.setDatos(residentes);
        cargarDesdeTabla();
    }

    /**
     * Carga en el formulario el residente seleccionado en la tabla.
     */
    private void cargarDesdeTabla() {
        Residente residente = modeloTabla.en(tabla.getSelectedRow());
        if (residente == null) {
            mostrarVehiculos(null);
            return;
        }
        campoId.setText(residente.getId());
        campoId.setEnabled(false);
        campoNombre.setText(residente.getNombre());
        campoCasa.setText(residente.getCasa());
        casillaSocio.setSelected(residente.isSocio());
        editando = true;
        mostrarVehiculos(residente);
    }

    /**
     * Muestra el detalle de vehículos de un residente.
     *
     * @param residente residente seleccionado o {@code null} para vaciar
     */
    private void mostrarVehiculos(Residente residente) {
        if (residente == null) {
            areaVehiculos.setText("(selecciona un residente)");
            return;
        }
        Vehiculo[] vehiculos = sistema.vehiculosDeResidente(residente.getId());
        StringBuilder texto = new StringBuilder();
        texto.append("Residente: ").append(residente.toString()).append('\n');
        if (vehiculos.length == 0) {
            texto.append("No tiene vehículos registrados.\n");
        } else {
            texto.append("Vehículos:\n");
            for (int i = 0; i < vehiculos.length; i++) {
                texto.append('•').append(' ').append(i + 1).append(". ")
                        .append(vehiculos[i].toString())
                        .append("  [").append(vehiculos[i].getTipo()).append("]\n");
            }
        }
        areaVehiculos.setText(texto.toString());
    }

    /**
     * Deja prontas los controles para registrar un residente nuevo.
     */
    private void modoNuevo() {
        campoId.setText("");
        campoId.setEnabled(true);
        campoNombre.setText("");
        campoCasa.setText("");
        casillaSocio.setSelected(false);
        editando = false;
        tabla.clearSelection();
        mostrarVehiculos(null);
        campoNombre.requestFocus();
    }

    /**
     * Guarda el residente del formulario: actualiza si viene de la tabla o
     * registra si es nuevo.
     */
    private void guardar() {
        String nombre = campoNombre.getText().trim();
        String casa = campoCasa.getText().trim();
        boolean socio = casillaSocio.isSelected();
        try {
            if (editando && !campoId.isEnabled()) {
                String id = campoId.getText().trim();
                sistema.actualizarResidente(id, nombre, casa, socio);
                JOptionPane.showMessageDialog(this,
                        "Residente " + id + " actualizado.",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                modoNuevo();
            } else {
                String id = campoId.getText().trim();
                Residente residente;
                if (id.isEmpty()) {
                    residente = sistema.registrarResidente(nombre, casa, socio);
                } else {
                    residente = sistema.registrarResidenteConId(id, nombre, casa, socio);
                }
                JOptionPane.showMessageDialog(this,
                        "Residente registrado con ID " + residente.getId(),
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                modoNuevo();
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "No se pudo guardar", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Elimina al residente del formulario (o del residente seleccionado).
     */
    private void eliminar() {
        String id = campoId.getText().trim();
        if (id.isEmpty()) {
            id = pedirId();
            if (id == null) {
                return;
            }
        }
        Residente residente = sistema.buscarResidentePorId(id);
        if (residente == null) {
            JOptionPane.showMessageDialog(this, "ID no existe.",
                    "No se pudo eliminar", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Eliminar a " + residente.getNombre() + " y sus vehículos?",
                "Confirmar", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (respuesta == JOptionPane.OK_OPTION) {
            try {
                sistema.eliminarResidente(id);
                modoNuevo();
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(),
                        "No se pudo eliminar", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    /**
     * Pide un ID por mensaje gráfico cuando se pulsa "Eliminar" sin residente
     * seleccionado.
     *
     * @return el ID o {@code null} si se cancela
     */
    private String pedirId() {
        return JOptionPane.showInputDialog(this, "ID del residente a eliminar:",
                "Eliminar residente", JOptionPane.QUESTION_MESSAGE);
    }

    /**
     * Realiza una búsqueda por ID o por nombre y actualiza la tabla.
     */
    private void buscar() {
        campoBusqueda.setText(campoBusqueda.getText().trim());
        actualizarDatos();
    }

    /** Refresca el panel por cambios que no provienen de tab. */
    public void actualizar() {
        actualizarDatos();
    }
}
