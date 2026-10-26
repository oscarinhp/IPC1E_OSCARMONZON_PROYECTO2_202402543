package modelo;

/**
 * Vehículo registrado en el sistema, ya sea de un residente o de un visitante.
 *
 * <p>Las placas son únicas en todo el sistema y se normalizan a mayúsculas.
 * Dos vehículos son iguales si tienen la misma placa.</p>
 */
public class Vehiculo {

    private final String placa;
    private final String marca;
    private final String modelo;
    private final String color;
    private final TipoVehiculo tipo;
    private final boolean visitante;
    private final String nombreVisitante;

    private EstadoVehiculo estado;
    private Residente residente;
    private EspacioParqueo espacioAsignado;

    /**
     * Crea el vehículo de un residente.
     *
     * @param placa  placa, se guarda en mayúsculas
     * @param marca  marca del vehículo
     * @param modelo modelo del vehículo
     * @param color  color del vehículo
     * @param tipo   tipo de vehículo
     */
    public Vehiculo(String placa, String marca, String modelo, String color,
                    TipoVehiculo tipo) {
        this(placa, marca, modelo, color, tipo, false, null);
    }

    /**
     * Crea el vehículo de un visitante temporal.
     *
     * @param placa           placa, se guarda en mayúsculas
     * @param marca           marca del vehículo
     * @param modelo          modelo del vehículo
     * @param color           color del vehículo
     * @param tipo            tipo de vehículo
     * @param nombreVisitante nombre de la persona que visita
     */
    public Vehiculo(String placa, String marca, String modelo, String color,
                    TipoVehiculo tipo, String nombreVisitante) {
        this(placa, marca, modelo, color, tipo, true, nombreVisitante);
    }

    private Vehiculo(String placa, String marca, String modelo, String color,
                     TipoVehiculo tipo, boolean visitante, String nombreVisitante) {
        this.placa = placa.toUpperCase();
        this.marca = marca;
        this.modelo = modelo;
        this.color = color;
        this.tipo = tipo;
        this.visitante = visitante;
        this.nombreVisitante = nombreVisitante;
        this.estado = EstadoVehiculo.FUERA;
    }

    /**
     * Indica si el vehículo puede ocupar el área de socios (filas A–C).
     *
     * <p>Solo los vehículos de residentes socios tienen acceso a esa área;
     * los visitantes y los residentes no socios usan las filas E–I.</p>
     *
     * @return {@code true} si el vehículo pertenece a un residente socio
     */
    public boolean puedeUsarAreaSocios() {
        return !visitante && residente != null && residente.isSocio();
    }

    public String getPlaca() {
        return placa;
    }

    public String getMarca() {
        return marca;
    }

    public String getModelo() {
        return modelo;
    }

    public String getColor() {
        return color;
    }

    public TipoVehiculo getTipo() {
        return tipo;
    }

    public EstadoVehiculo getEstado() {
        return estado;
    }

    public void setEstado(EstadoVehiculo estado) {
        this.estado = estado;
    }

    public boolean isVisitante() {
        return visitante;
    }

    public String getNombreVisitante() {
        return nombreVisitante;
    }

    public Residente getResidente() {
        return residente;
    }

    public void setResidente(Residente residente) {
        this.residente = residente;
    }

    /**
     * Espacio de parqueo asignado al vehículo, si está estacionado.
     *
     * @return el espacio o {@code null} si no está en el parqueo
     */
    public EspacioParqueo getEspacioAsignado() {
        return espacioAsignado;
    }

    public void setEspacioAsignado(EspacioParqueo espacioAsignado) {
        this.espacioAsignado = espacioAsignado;
    }

    /**
     * Nombre del propietario: si es visitante devuelve su nombre de visita,
     * en otro caso el nombre del residente dueño.
     *
     * @return nombre del propietario
     */
    public String getPropietario() {
        if (visitante) {
            return nombreVisitante + " (Visitor)";
        }
        if (residente == null) {
            return "Sin propietario";
        }
        return residente.toString();
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (!(objeto instanceof Vehiculo)) {
            return false;
        }
        Vehiculo otro = (Vehiculo) objeto;
        return placa.equals(otro.placa);
    }

    @Override
    public int hashCode() {
        return placa.hashCode();
    }

    @Override
    public String toString() {
        return placa + " - " + marca + " " + modelo;
    }
}
