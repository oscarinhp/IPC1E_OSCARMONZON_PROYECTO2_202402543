package modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Evento del historial del sistema. Cada evento guarda el momento, el tipo, la
 * descripción, la placa del vehículo involucrado (si aplica) y el espacio
 * asignado o liberado (si aplica).
 */
public class Evento {

    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final LocalDateTime momento;
    private final TipoEvento tipo;
    private final String descripcion;
    private final String placa;
    private final String espacio;

    /**
     * Crea un evento con todos sus campos.
     *
     * @param tipo        tipo del evento
     * @param descripcion texto descriptivo
     * @param placa       placa del vehículo o {@code null} si no aplica
     * @param espacio     identificador del espacio o {@code null} si no aplica
     */
    public Evento(TipoEvento tipo, String descripcion, String placa, String espacio) {
        this.momento = LocalDateTime.now();
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.placa = placa;
        this.espacio = espacio;
    }

    public TipoEvento getTipo() {
        return tipo;
    }

    public String getPlaca() {
        return placa;
    }

    public String getEspacio() {
        return espacio;
    }

    public String getFechaHora() {
        return momento.format(FORMATO);
    }

    @Override
    public String toString() {
        StringBuilder texto = new StringBuilder("[");
        texto.append(getFechaHora()).append("] ");
        texto.append(tipo);
        if (placa != null) {
            texto.append(" | Placa: ").append(placa);
        }
        if (espacio != null) {
            texto.append(" | Espacio: ").append(espacio);
        }
        texto.append(" - ").append(descripcion);
        return texto.toString();
    }
}
