package vista;

import java.awt.Color;
import java.awt.Font;

/**
 * Paleta y tipografías compartidas por los paneles para mantener una interfaz
 * coherente.
 */
public final class Tema {

    /** Color de fondo de los paneles. */
    public static final Color COLOR_FONDO = new Color(0xF4F6F8);

    /** Color para encabezados y áreas laterales. */
    public static final Color COLOR_PANEL_LATERAL = new Color(0xE6EBF0);

    /** Espacios de la zona de socios: azul claro. */
    public static final Color COLOR_SOCIO = new Color(0x9AD1F2);

    /** Espacios de la zona general: verde claro. */
    public static final Color COLOR_GENERAL = new Color(0xB6E3A8);

    /** Espacio ocupado por un residente: naranja. */
    public static final Color COLOR_OCUPADO = new Color(0xFFB84D);

    /** Espacio ocupado por un visitante: rosado. */
    public static final Color COLOR_OCUPADO_VISITANTE = new Color(0xF4A6A6);

    /** Estado "operativa" de varias garitas: verde. */
    public static final Color COLOR_OK = new Color(0x8FD694);

    /** Estado "libre" de varias garitas: gris. */
    public static final Color COLOR_LIBRE = new Color(0xDDE3EA);

    /** Texto de error. */
    public static final Color COLOR_ERROR = new Color(0xD9534F);

    public static final Font FUENTE_TITULO = new Font("SansSerif", Font.BOLD, 20);
    public static final Font FUENTE_ESTADO = new Font("SansSerif", Font.BOLD, 13);
    public static final Font FUENTE_NORMAL = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FUENTE_MONO = new Font("Consolas", Font.PLAIN, 12);
    public static final Font FUENTE_TABLA = new Font("SansSerif", Font.PLAIN, 13);

    private Tema() {
    }
}
