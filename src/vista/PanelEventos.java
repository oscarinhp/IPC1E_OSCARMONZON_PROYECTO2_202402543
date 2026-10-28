package vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import modelo.Evento;
import modelo.SistemaParqueo;
import modelo.TipoEvento;

/**
 * Panel del historial de eventos: los últimos movimientos del sistema vistos
 * desde la pila LIFO, es decir, el evento más reciente arriba.
 *
 * <p>Permite filtrar por tipo de evento y limitar la cantidad mostrada, para
 * evidenciar el comportamiento de la pila ({@code ultimosEventos}).</p>
 */
public class PanelEventos extends JPanel {

    /** Límite con el que se ve todo el historial. */
    private static final int TODOS = 0;

    private final SistemaParqueo sistema;

    private final JComboBox<TipoEvento> comboFiltro = new JComboBox<>();
    private final JComboBox<Limite> comboLimite = new JComboBox<>(Limite.values());
    private final JTextArea areaEventos = new JTextArea();
    private final JLabel etiquetaTotal = new JLabel(" ");

    /** Opciones de cuántos eventos ver. */
    private enum Limite {
        /** Los últimos 10. */
        ULTIMOS_10("Últimos 10", 10),
        /** Los últimos 25. */
        ULTIMOS_25("Últimos 25", 25),
        /** Los últimos 50. */
        ULTIMOS_50("Últimos 50", 50),
        /** Todo el historial. */
        TODOS_LOS_EVENTOS("Todo el historial", TODOS);

        private final String etiqueta;
        private final int cantidad;

        Limite(String etiqueta, int cantidad) {
            this.etiqueta = etiqueta;
            this.cantidad = cantidad;
        }

        @Override
        public String toString() {
            return etiqueta;
        }
    }

    /**
     * Crea el panel.
     *
     * @param sistema núcleo del sistema
     */
    public PanelEventos(SistemaParqueo sistema) {
        this.sistema = sistema;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        barra.add(new JLabel("Tipo:"));
        comboFiltro.addItem(null);
        for (TipoEvento tipo : TipoEvento.values()) {
            comboFiltro.addItem(tipo);
        }
        barra.add(comboFiltro);
        barra.add(new JLabel("Cantidad:"));
        comboLimite.setSelectedIndex(1);
        barra.add(comboLimite);
        barra.add(etiquetaTotal);

        comboFiltro.addActionListener(e -> actualizar());
        comboLimite.addActionListener(e -> actualizar());

        add(barra, BorderLayout.NORTH);

        areaEventos.setEditable(false);
        areaEventos.setFont(Tema.FUENTE_MONO);
        areaEventos.setLineWrap(true);
        areaEventos.setWrapStyleWord(false);
        add(new JScrollPane(areaEventos), BorderLayout.CENTER);

        actualizar();
    }

    /**
     * Vuelve a leer la pila de eventos según el filtro y el límite elegidos.
     */
    public final void actualizar() {
        TipoEvento tipoElegido = (TipoEvento) comboFiltro.getSelectedItem();
        Limite limiteElegido = (Limite) comboLimite.getSelectedItem();

        Evento[] eventos;
        if (tipoElegido == null) {
            eventos = sistema.ultimosEventos(limiteElegido.cantidad);
        } else {
            eventos = filtrar(tipoElegido, limiteElegido.cantidad);
        }

        StringBuilder texto = new StringBuilder();
        if (eventos.length == 0) {
            texto.append("(sin eventos todavía; los registra el sistema al "
                    + "registrar visitantes y mover vehículos)");
        } else {
            for (Evento evento : eventos) {
                texto.append(evento.toString()).append('\n');
            }
        }
        areaEventos.setText(texto.toString());
        areaEventos.setCaretPosition(0);
        etiquetaTotal.setText("Historial LIFO: " + sistema.cantidadEventos()
                + " eventos guardados");
    }

    /**
     * Filtra los últimos eventos por tipo recorriendo la pila desde arriba.
     *
     * @param tipo   tipo buscado
     * @param limite cuántos ELEMENTOS revisar como máximo ({@code 0} = todos)
     * @return arreglo con los eventos del tipo pedido, más recientes primero
     */
    private Evento[] filtrar(TipoEvento tipo, int limite) {
        Evento[] todos = sistema.ultimosEventos(limite);
        int cantidad = 0;
        for (Evento evento : todos) {
            if (evento.getTipo() == tipo) {
                cantidad++;
            }
        }
        Evento[] resultado = new Evento[cantidad];
        int indice = 0;
        for (Evento evento : todos) {
            if (evento.getTipo() == tipo) {
                resultado[indice++] = evento;
            }
        }
        return resultado;
    }
}
