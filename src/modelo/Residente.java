package modelo;

import estructuras.ListaSimple;

/**
 * Residente del parque residencial.
 *
 * <p>Guarda su identificación, nombre, número de casa, si es socio y su lista
 * propia de vehículos (implementada con {@code ListaSimple}). Un residente
 * puede tener de 0 a 3 vehículos; intentar agregar un cuarto lanza una
 * {@link IllegalArgumentException}.</p>
 */
public class Residente {

    /** Número máximo de vehículos que puede registrar un residente. */
    public static final int MAXIMO_VEHICULOS = 3;

    private final String id;
    private String nombre;
    private String casa;
    private boolean socio;
    private final ListaSimple vehiculos;

    /**
     * Crea un residente con sus datos básicos.
     *
     * @param id     identificación única del residente (por ejemplo {@code R001})
     * @param nombre nombre del residente
     * @param casa   número de casa
     * @param socio  {@code true} si el residente paga cuota de socio
     */
    public Residente(String id, String nombre, String casa, boolean socio) {
        this.id = id;
        this.nombre = nombre;
        this.casa = casa;
        this.socio = socio;
        this.vehiculos = new ListaSimple();
    }

    /**
     * Agrega un vehículo a la lista del residente.
     *
     * <p>Valida las reglas de negocio: no se admiten vehículos nulos, no se
     * puede repetir una placa y no se puede superar el límite de 3 vehículos
     * por residente.</p>
     *
     * @param vehiculo vehículo a registrar
     * @throws IllegalArgumentException si alguna regla no se cumple
     */
    public void agregarVehiculo(Vehiculo vehiculo) {
        if (vehiculo == null) {
            throw new IllegalArgumentException("El vehículo no puede ser nulo.");
        }
        if (vehiculos.getTamanio() >= MAXIMO_VEHICULOS) {
            throw new IllegalArgumentException(
                    "El residente " + nombre + " ya tiene el máximo de "
                            + MAXIMO_VEHICULOS + " vehículos.");
        }
        if (buscarVehiculoPorPlaca(vehiculo.getPlaca()) != null) {
            throw new IllegalArgumentException(
                    "La placa " + vehiculo.getPlaca()
                            + " ya está registrada para este residente.");
        }
        vehiculo.setResidente(this);
        vehiculos.agregar(vehiculo);
    }

    /**
     * Busca un vehículo de este residente por su placa.
     *
     * @param placa placa buscada (sin distinguir mayúsculas)
     * @return el vehículo o {@code null} si no existe
     */
    public Vehiculo buscarVehiculoPorPlaca(String placa) {
        if (placa == null) {
            return null;
        }
        Object[] datos = vehiculos.aArreglo();
        for (Object dato : datos) {
            Vehiculo vehiculo = (Vehiculo) dato;
            if (vehiculo.getPlaca().equals(placa.toUpperCase())) {
                return vehiculo;
            }
        }
        return null;
    }

    /**
     * Elimina un vehículo de la lista del residente.
     *
     * @param placa placa del vehículo a eliminar
     * @return {@code true} si se eliminó
     */
    public boolean eliminarVehiculo(String placa) {
        if (placa == null) {
            return false;
        }
        return vehiculos.eliminar(buscarVehiculoPorPlaca(placa));
    }

    /**
     * Indica si algún vehículo del residente está activo en el sistema
     * (en cola, en parqueo o saliendo).
     *
     * @return {@code true} si hay al menos un vehículo activo
     */
    public boolean tieneVehiculoActivo() {
        Object[] datos = vehiculos.aArreglo();
        for (Object dato : datos) {
            Vehiculo vehiculo = (Vehiculo) dato;
            if (vehiculo.getEstado() != EstadoVehiculo.FUERA) {
                return true;
            }
        }
        return false;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCasa() {
        return casa;
    }

    public void setCasa(String casa) {
        this.casa = casa;
    }

    public boolean isSocio() {
        return socio;
    }

    public void setSocio(boolean socio) {
        this.socio = socio;
    }

    public ListaSimple getVehiculos() {
        return vehiculos;
    }

    /**
     * Número de vehículos registrados por el residente.
     *
     * @return tamaño de su lista de vehículos
     */
    public int getNumeroVehiculos() {
        return vehiculos.getTamanio();
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (!(objeto instanceof Residente)) {
            return false;
        }
        Residente otro = (Residente) objeto;
        return id.equals(otro.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return id + " - " + nombre + " (Casa " + casa + ")"
                + (socio ? " [Socio]" : " [No socio]");
    }
}
