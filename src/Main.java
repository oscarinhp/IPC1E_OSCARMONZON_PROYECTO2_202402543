import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * Punto de entrada del Sistema de Gestión de Parqueo del Parque Residencial.
 *
 * <p>En esta primera etapa solo se muestra la ventana base. Las siguientes
 * etapas agregan las estructuras de datos, el modelo, los hilos, la
 * persistencia y los paneles definitivos.</p>
 *
 * @author Oscar Monzón - Carné 202402543
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame ventana = new JFrame("Parque Residencial - Proyecto 2");
            ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ventana.setSize(960, 640);
            ventana.setLocationRelativeTo(null);
            ventana.setLayout(new BorderLayout());

            JLabel titulo = new JLabel(
                    "Sistema de Gestión de Parqueo",
                    SwingConstants.CENTER);
            titulo.setFont(new Font("SansSerif", Font.BOLD, 28));
            titulo.setBorder(BorderFactory.createEmptyBorder(40, 20, 10, 20));

            JLabel subtitulo = new JLabel(
                    "IPC1E - Proyecto 2 - En construcción",
                    SwingConstants.CENTER);
            subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 16));

            JPanel centro = new JPanel(new BorderLayout());
            centro.add(titulo, BorderLayout.CENTER);
            centro.add(subtitulo, BorderLayout.SOUTH);

            JLabel pie = new JLabel(
                    "Oscar Monzón - 202402543",
                    SwingConstants.CENTER);
            pie.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));

            ventana.add(centro, BorderLayout.CENTER);
            ventana.add(pie, BorderLayout.SOUTH);
            ventana.setVisible(true);
        });
    }
}
