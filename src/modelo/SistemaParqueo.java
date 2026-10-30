package modelo;

import estructuras.Cola;
import estructuras.ListaDoble;
import estructuras.ListaSimple;
import estructuras.NodoDoble;
import estructuras.Pila;

/**
 * Núcleo del sistema: guarda el estado compartido por los hilos de las garitas
 * y la interfaz.
 *
 * <p>Todos los métodos que modifican residentes, vehículos, colas o espacios
 * están sincronizados para evitar condiciones de carrera. Las colas usan
 * {@code wait()} dentro de un ciclo {@code while} cuando están vacías y
 * {@code notifyAll()} al encolar vehículos.</p>
 */
public class SistemaParqueo {

    /** Garitas de entrada que atienden la cola de acceso. */
    public static final int GARITAS_ENTRADA = 3;

    /** Garitas de salida que atienden la cola de salida. */
    public static final int GARITAS_SALIDA = 2;

    private final ListaDoble residentes = new ListaDoble();
    private final ListaSimple visitantes = new ListaSimple();
    private final Cola colaEntrada = new Cola();
    private final Cola colaSalida = new Cola();
    private final Pila eventos = new Pila();
    private final Parqueo parqueo = new Parqueo();

    private volatile boolean activo = false;
    private Runnable observador;

    /**
     * Registra un residente generando su ID de forma automática.
     *
     * @param nombre nombre del residente
     * @param casa   número de casa o lote
     * @param socio  {@code true} si es socio
     * @return el residente registrado
     */
    public Residente registrarResidente(String nombre, String casa, boolean socio) {
        return registrarResidenteConId(generarSiguienteIdResidente(), nombre, casa, socio);
    }

    /**
     * Registra un residente con un ID indicado. Se usa al reconstruir las
     * listas desde los archivos de persistencia.
     *
     * @param id     identificación única
     * @param nombre nombre del residente
     * @param casa   número de casa o lote
     * @param socio  {@code true} si es socio
     * @return el residente registrado
     */
    public Residente registrarResidenteConId(String id, String nombre, String casa,
                                             boolean socio) {
        synchronized (this) {
            if (nombre == null || nombre.isBlank()) {
                throw new IllegalArgumentException("Se requiere el nombre del residente.");
            }
            if (casa == null || casa.isBlank()) {
                throw new IllegalArgumentException("Se requiere el número de casa.");
            }
            if (nombre.contains("|") || casa.contains("|") || id.contains("|")) {
                throw new IllegalArgumentException(
                        "El separador | está reservado para los archivos de datos.");
            }
            Residente residente = new Residente(id, nombre, casa, socio);
            if (residentes.contiene(residente)) {
                throw new IllegalArgumentException(
                        "El ID " + id + " ya pertenece a otro residente.");
            }
            residentes.agregar(residente);
            notificarCambio();
            return residente;
        }
    }

    /**
     * Actualiza los datos de un residente, manteniendo su posición en la
     * lista.
     *
     * @param id     id del residente a actualizar
     * @param nombre nuevo nombre
     * @param casa   nuevo número de casa
     * @param socio  nuevo indicador de socio
     * @return {@code true} si se actualizó
     */
    public boolean actualizarResidente(String id, String nombre, String casa, boolean socio) {
        synchronized (this) {
            Residente actual = buscarResidentePorId(id);
            if (actual == null) {
                return false;
            }
            actual.setNombre(nombre);
            actual.setCasa(casa);
            actual.setSocio(socio);
            notificarCambio();
            return true;
        }
    }

    /**
     * Elimina un residente con todos sus vehículos.
     *
     * <p>Se rechaza si algún vehículo del residente está en una cola o
     * estacionado, porque entonces sigue activo dentro del sistema.</p>
     *
     * @param id id del residente
     * @return {@code true} si se eliminó
     * @throws IllegalArgumentException si el residente no existe o tiene
     *                                  vehículos activos
     */
    public boolean eliminarResidente(String id) {
        synchronized (this) {
            Residente residente = buscarResidentePorId(id);
            if (residente == null) {
                throw new IllegalArgumentException(
                        "No existe un residente con el ID " + id + ".");
            }
            if (residente.tieneVehiculoActivo()) {
                throw new IllegalArgumentException(
                        "No se puede eliminar " + residente.getNombre()
                                + " porque tiene vehículos en el parqueo o en colas.");
            }
            boolean eliminado = residentes.eliminar(residente);
            notificarCambio();
            return eliminado;
        }
    }

    /**
     * Busca un residente por su ID recorriendo la lista doble.
     *
     * @param id id buscado
     * @return el residente o {@code null} si no existe
     */
    public synchronized Residente buscarResidentePorId(String id) {
        if (id == null) {
            return null;
        }
        NodoDoble actual = residentes.getInicio();
        while (actual != null) {
            Residente residente = (Residente) actual.getDato();
            if (residente.getId().equals(id)) {
                return residente;
            }
            actual = actual.getSiguiente();
        }
        return null;
    }

    /**
     * Busca a los residentes cuyo nombre coincide (sin distinguir
     * mayúsculas).
     *
     * @param nombre nombre buscado
     * @return arreglo con los residentes que coinciden
     */
    public synchronized Residente[] buscarResidentesPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return new Residente[0];
        }
        ListaSimple coincidencias = new ListaSimple();
        Residente[] residentesArreglo = listarResidentes();
        for (Residente residente : residentesArreglo) {
            if (residente.getNombre().toLowerCase().contains(nombre.toLowerCase())) {
                coincidencias.agregar(residente);
            }
        }
        Residente[] resultado = new Residente[coincidencias.getTamanio()];
        Object[] datos = coincidencias.aArreglo();
        for (int i = 0; i < datos.length; i++) {
            resultado[i] = (Residente) datos[i];
        }
        return resultado;
    }

    /**
     * Lista los residentes de inicio a fin.
     *
     * @return arreglo de residentes
     */
    public synchronized Residente[] listarResidentes() {
        Residente[] resultado = new Residente[residentes.getTamanio()];
        Object[] datos = residentes.aArreglo();
        for (int i = 0; i < datos.length; i++) {
            resultado[i] = (Residente) datos[i];
        }
        return resultado;
    }

    /**
     * Lista los residentes de fin a inicio, para demostrar el recorrido en
     * ambos sentidos de la lista doble.
     *
     * @return arreglo de residentes en orden inverso
     */
    public synchronized Residente[] listarResidentesInverso() {
        Residente[] resultado = new Residente[residentes.getTamanio()];
        Object[] datos = residentes.aArregloInverso();
        for (int i = 0; i < datos.length; i++) {
            resultado[i] = (Residente) datos[i];
        }
        return resultado;
    }

    /**
     * Registra un vehículo para un residente.
     *
     * <p>Valida que el residente exista, que la placa no exista aún en el
     * sistema y que el residente no supere los 3 vehículos.</p>
     *
     * @param idResidente id del residente dueño
     * @param placa       placa del vehículo
     * @param marca       marca
     * @param modelo      modelo
     * @param color       color
     * @param tipo        tipo de vehículo
     * @return el vehículo registrado
     */
    public Vehiculo registrarVehiculo(String idResidente, String placa, String marca,
                                      String modelo, String color, TipoVehiculo tipo) {
        synchronized (this) {
            Residente residente = buscarResidentePorId(idResidente);
            if (residente == null) {
                throw new IllegalArgumentException(
                        "No existe un residente con el ID " + idResidente + ".");
            }
            if (existePlaca(placa)) {
                throw new IllegalArgumentException(
                        "La placa " + placa + " ya está en uso en el sistema.");
            }
            if (placa == null || marca == null || modelo == null || color == null
                    || placa.isBlank() || marca.isBlank() || modelo.isBlank()
                    || color.isBlank()) {
                throw new IllegalArgumentException(
                        "Todos los datos del vehículo son obligatorios.");
            }
            if (placa.contains("|") || marca.contains("|") || modelo.contains("|")
                    || color.contains("|")) {
                throw new IllegalArgumentException(
                        "El separador | está reservado para los archivos de datos.");
            }
            Vehiculo vehiculo = new Vehiculo(placa, marca, modelo, color, tipo);
            residente.agregarVehiculo(vehiculo);
            notificarCambio();
            return vehiculo;
        }
    }

    /**
     * Elimina un vehículo de un residente. Solo se permite si el vehículo no
     * está activo (en cola o estacionado).
     *
     * @param idResidente id del residente dueño
     * @param placa       placa del vehículo a eliminar
     * @return {@code true} si se eliminó
     */
    public boolean eliminarVehiculo(String idResidente, String placa) {
        synchronized (this) {
            Residente residente = buscarResidentePorId(idResidente);
            if (residente == null) {
                return false;
            }
            Vehiculo vehiculo = residente.buscarVehiculoPorPlaca(placa);
            if (vehiculo == null) {
                return false;
            }
            if (vehiculo.getEstado() != EstadoVehiculo.FUERA) {
                throw new IllegalArgumentException(
                        "El vehículo " + vehiculo.getPlaca()
                                + " sigue activo en el sistema.");
            }
            boolean eliminado = residente.eliminarVehiculo(placa);
            notificarCambio();
            return eliminado;
        }
    }

    /**
     * Busca un vehículo en todo el sistema por su placa (residentes y
     * visitantes).
     *
     * @param placa placa buscada
     * @return el vehículo o {@code null} si no existe
     */
    public synchronized Vehiculo buscarVehiculoPorPlaca(String placa) {
        if (placa == null) {
            return null;
        }
        String placaBuscada = placa.toUpperCase();
        Residente[] residentesArreglo = listarResidentes();
        for (Residente residente : residentesArreglo) {
            Vehiculo vehiculo = residente.buscarVehiculoPorPlaca(placaBuscada);
            if (vehiculo != null) {
                return vehiculo;
            }
        }
        Object[] visitantesArreglo = visitantes.aArreglo();
        for (Object dato : visitantesArreglo) {
            Vehiculo visitante = (Vehiculo) dato;
            if (visitante.getPlaca().equals(placaBuscada)) {
                return visitante;
            }
        }
        return null;
    }

    /**
     * Indica si la placa ya existe en el sistema.
     *
     * @param placa placa a validar
     * @return {@code true} si ya está en uso
     */
    private synchronized boolean existePlaca(String placa) {
        return buscarVehiculoPorPlaca(placa) != null;
    }

    /**
     * Vehículos de un residente.
     *
     * @param idResidente id del residente
     * @return arreglo de vehículos (puede ser vacío)
     */
    public synchronized Vehiculo[] vehiculosDeResidente(String idResidente) {
        Residente residente = buscarResidentePorId(idResidente);
        if (residente == null) {
            return new Vehiculo[0];
        }
        Object[] datos = residente.getVehiculos().aArreglo();
        Vehiculo[] resultado = new Vehiculo[datos.length];
        for (int i = 0; i < datos.length; i++) {
            resultado[i] = (Vehiculo) datos[i];
        }
        return resultado;
    }

    /**
     * Vehículos de residentes que están fuera del parqueo y pueden solicitar
     * ingreso.
     *
     * @return arreglo de vehículos disponibles
     */
    public synchronized Vehiculo[] vehiculosListosParaEntrar() {
        ListaSimple disponibles = new ListaSimple();
        Residente[] residentesArreglo = listarResidentes();
        for (Residente residente : residentesArreglo) {
            Vehiculo[] vehiculos = vehiculosDeResidente(residente.getId());
            for (Vehiculo vehiculo : vehiculos) {
                if (vehiculo.getEstado() == EstadoVehiculo.FUERA) {
                    disponibles.agregar(vehiculo);
                }
            }
        }
        Vehiculo[] resultado = new Vehiculo[disponibles.getTamanio()];
        Object[] datos = disponibles.aArreglo();
        for (int i = 0; i < datos.length; i++) {
            resultado[i] = (Vehiculo) datos[i];
        }
        return resultado;
    }

    /**
     * Registra a un visitante y lo manda directamente a la cola de entrada.
     *
     * <p>El visitante es información temporal: se quita de la lista al salir
     * y nunca se guarda en los archivos de persistencia.</p>
     *
     * @param nombre  nombre de la persona que visita
     * @param placa   placa del vehículo
     * @param marca   marca
     * @param modelo  modelo
     * @param color   color
     * @param tipo    tipo de vehículo
     * @return el vehículo del visitante
     */
    public Vehiculo registrarVisitante(String nombre, String placa, String marca,
                                       String modelo, String color, TipoVehiculo tipo) {
        synchronized (this) {
            if (nombre == null || nombre.isBlank()) {
                throw new IllegalArgumentException("Se requiere el nombre del visitante.");
            }
            if (existePlaca(placa)) {
                throw new IllegalArgumentException(
                        "La placa " + placa + " ya está en uso en el sistema.");
            }
            Vehiculo visitante = new Vehiculo(placa, marca, modelo, color, tipo, nombre);
            visitantes.agregar(visitante);
            eventos.apilar(new Evento(TipoEvento.VISITANTE,
                    "El visitante " + nombre + " solicitó ingreso",
                    visitante.getPlaca(), null));
            notificarCambio();
            return visitante;
        }
    }

    /**
     * Solicita el ingreso de un vehículo a la cola de entrada.
     *
     * @param placa placa del vehículo
     * @throws IllegalArgumentException si el vehículo no existe o ya está
     *                                  activo
     */
    public void solicitarEntrada(String placa) {
        synchronized (this) {
            Vehiculo vehiculo = buscarVehiculoPorPlaca(placa);
            if (vehiculo == null) {
                throw new IllegalArgumentException(
                        "No existe un vehículo con la placa " + placa + ".");
            }
            if (vehiculo.getEstado() != EstadoVehiculo.FUERA) {
                throw new IllegalArgumentException(
                        "El vehículo " + vehiculo.getPlaca()
                                + " ya está en una cola o estacionado");
            }
            vehiculo.setEstado(EstadoVehiculo.EN_COLA_ENTRADA);
            colaEntrada.encolar(vehiculo);
            notificarCambio();
        }
    }

    /**
     * Solicita la salida de un vehículo estacionado, colocándolo en la cola de
     * salida.
     *
     * @param placa placa del vehículo
     * @throws IllegalArgumentException si el vehículo no existe o no está
     *                                  estacionado
     */
    public void solicitarSalida(String placa) {
        synchronized (this) {
            Vehiculo vehiculo = buscarVehiculoPorPlaca(placa);
            if (vehiculo == null) {
                throw new IllegalArgumentException(
                        "No existe un vehículo con la placa " + placa + ".");
            }
            if (vehiculo.getEstado() != EstadoVehiculo.EN_PARQUEO) {
                throw new IllegalArgumentException(
                        "El vehículo " + vehiculo.getPlaca()
                                + " no está estacionado en estos momentos.");
            }
            vehiculo.setEstado(EstadoVehiculo.EN_COLA_SALIDA);
            colaSalida.encolar(vehiculo);
            notificarCambio();
        }
    }

    /**
     * La siguiente garita de entrada toma un vehículo de la cola. Si la cola
     * está vacía, el hilo espera con {@code wait()}.
     *
     * @return el vehículo que atiende la garita
     * @throws InterruptedException si el hilo es interrumpido
     */
    public Vehiculo tomarDeColaEntrada() throws InterruptedException {
        return (Vehiculo) colaEntrada.desencolar();
    }

    /**
     * La siguiente garita de salida toma un vehículo de la cola de salida.
     *
     * @return el vehículo que atiende la garita
     * @throws InterruptedException si el hilo es interrumpido
     */
    public Vehiculo tomarDeColaSalida() throws InterruptedException {
        return (Vehiculo) colaSalida.desencolar();
    }

    /**
     * Procesa la entrada de un vehículo: busca un espacio de la zona
     * autorizada y lo asigna, o lo rechaza si no hay espacio.
     *
     * @param vehiculo vehículo que la garita está atendiendo
     * @return {@code true} si recibió un espacio; {@code false} si fue
     *         rechazado
     */
    public boolean procesarEntrada(Vehiculo vehiculo) {
        synchronized (this) {
            EspacioParqueo espacio = parqueo.asignarEspacio(vehiculo);
            if (espacio == null) {
                vehiculo.setEstado(EstadoVehiculo.FUERA);
                if (vehiculo.isVisitante()) {
                    quitarVisitante(vehiculo);
                }
                eventos.apilar(new Evento(TipoEvento.RECHAZO,
                        "No hay espacio libre en su zona",
                        vehiculo.getPlaca(), null));
                return false;
            }
            vehiculo.setEstado(EstadoVehiculo.EN_PARQUEO);
            eventos.apilar(new Evento(TipoEvento.INGRESO,
                    "La garita de entrada lo admitió",
                    vehiculo.getPlaca(), null));
            eventos.apilar(new Evento(TipoEvento.ASIGNACION,
                    "Asignado a un espacio de parqueo",
                    vehiculo.getPlaca(), espacio.getId()));
            return true;
        }
    }

    /**
     * Procesa la salida de un vehículo: libera su espacio y saca a los
     * visitantes de la lista temporal.
     *
     * @param vehiculo vehículo que la garita está atendiendo
     * @return {@code true} si se liberó su espacio
     */
    public boolean procesarSalida(Vehiculo vehiculo) {
        synchronized (this) {
            EspacioParqueo espacio = parqueo.buscarEspacioDeVehiculo(vehiculo.getPlaca());
            boolean asignado = espacio != null;
            if (asignado) {
                parqueo.liberarEspacio(vehiculo.getPlaca());
                eventos.apilar(new Evento(TipoEvento.LIBERACION,
                        "El espacio " + espacio.getId() + " quedó libre",
                        vehiculo.getPlaca(), espacio.getId()));
            }
            vehiculo.setEstado(EstadoVehiculo.FUERA);
            vehiculo.setEspacioAsignado(null);
            if (vehiculo.isVisitante()) {
                quitarVisitante(vehiculo);
            }
            eventos.apilar(new Evento(TipoEvento.SALIDA,
                    "La garita de salida lo retiró",
                    vehiculo.getPlaca(), null));
            return asignado;
        }
    }

    /**
     * Quita a un visitante de la lista temporal de visitantes. La lista se
     * restringe a esta vista porque es un sistema local, no una base de
     * datos.
     *
     * @param vehiculo vehículo del visitante a retirar
     */
    private synchronized void quitarVisitante(Vehiculo vehiculo) {
        visitantes.eliminar(vehiculo);
    }

    /**
     * Copia los visitantes activos del sistema (información temporal).
     *
     * @return arreglo de visitantes activos
     */
    public synchronized Vehiculo[] visitantesActivos() {
        Object[] datos = visitantes.aArreglo();
        Vehiculo[] resultado = new Vehiculo[datos.length];
        for (int i = 0; i < datos.length; i++) {
            resultado[i] = (Vehiculo) datos[i];
        }
        return resultado;
    }

    /**
     * Vehículos estacionados en este momento.
     *
     * @return arreglo de vehículos con espacio asignado
     */
    public synchronized Vehiculo[] vehiculosEstacionados() {
        EspacioParqueo[] espacios = parqueo.espaciosEnOrden();
        ListaSimple estacionados = new ListaSimple();
        for (EspacioParqueo espacio : espacios) {
            if (espacio.getVehiculo() != null) {
                estacionados.agregar(espacio.getVehiculo());
            }
        }
        Vehiculo[] resultado = new Vehiculo[estacionados.getTamanio()];
        Object[] datos = estacionados.aArreglo();
        for (int i = 0; i < datos.length; i++) {
            resultado[i] = (Vehiculo) datos[i];
        }
        return resultado;
    }

    /**
     * Últimos eventos del historial en orden LIFO: el más reciente primero.
     *
     * @param limite cuántos eventos se quieren;{@code 0} devuelve todos
     * @return arreglo de eventos
     */
    public synchronized Evento[] ultimosEventos(int limite) {
        Object[] datos = eventos.aArreglo();
        int cantidad = datos.length;
        if (limite > 0 && limite < cantidad) {
            cantidad = limite;
        }
        Evento[] resultado = new Evento[cantidad];
        for (int i = 0; i < cantidad; i++) {
            resultado[i] = (Evento) datos[i];
        }
        return resultado;
    }

    /**
     * Cantidad de eventos almacenados en la pila.
     *
     * @return tamaño del historial
     */
    public synchronized int cantidadEventos() {
        return eventos.getTamanio();
    }

    /**
     * Espacios libres de una zona.
     *
     * @param tipo zona (socio o general)
     * @return cantidad de espacios libres
     */
    public int contarEspaciosLibres(TipoEspacio tipo) {
        return parqueo.contarLibres(tipo);
    }

    /**
     * Espacios ocupados de una zona.
     *
     * @param tipo zona (socio o general)
     * @return cantidad de espacios ocupados
     */
    public int contarEspaciosOcupados(TipoEspacio tipo) {
        return parqueo.contarOcupados(tipo);
    }

    /**
     * Vehículos que esperan en la cola de entrada.
     *
     * @return cantidad en cola
     */
    public int tamanoColaEntrada() {
        return colaEntrada.getTamanio();
    }

    /**
     * Vehículos que esperan en la cola de salida.
     *
     * @return cantidad en cola
     */
    public int tamanoColaSalida() {
        return colaSalida.getTamanio();
    }

    /**
     * Snapshot de la cola de entrada para la vista.
     *
     * @return arreglo de vehículos del primero al último
     */
    public Vehiculo[] snapshotsColaEntrada() {
        Object[] datos = colaEntrada.aArreglo();
        Vehiculo[] resultado = new Vehiculo[datos.length];
        for (int i = 0; i < datos.length; i++) {
            resultado[i] = (Vehiculo) datos[i];
        }
        return resultado;
    }

    /**
     * Snapshot de la cola de salida para la vista.
     *
     * @return arreglo de vehículos del primero al último
     */
    public Vehiculo[] snapshotsColaSalida() {
        Object[] datos = colaSalida.aArreglo();
        Vehiculo[] resultado = new Vehiculo[datos.length];
        for (int i = 0; i < datos.length; i++) {
            resultado[i] = (Vehiculo) datos[i];
        }
        return resultado;
    }

    public Parqueo getParqueo() {
        return parqueo;
    }

    /* ---------- Control del estado del sistema ---------- */

    /**
     * Genera un ID no usado con formato {@code R###}.
     *
     * @return el nuevo id
     */
    private String generarSiguienteIdResidente() {
        int numero = residentes.getTamanio() + 1;
        while (buscarResidentePorId(formatearId(numero)) != null) {
            numero++;
        }
        return formatearId(numero);
    }

    private String formatearId(int numero) {
        return String.format("R%03d", numero);
    }

    /** Indica si las garitas están funcionando. */
    public boolean isActivo() {
        return activo;
    }

    /** Activa o desactiva la simulación de las garitas. */
    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    /**
     * Registra la función que refresca la interfaz gráfica cuando cambie el
     * sistema. La vista construye esa función con {@code SwingUtilities
     * .invokeLater} antes de iniciar las garitas.
     *
     * @param observador acción de refresco de la vista
     */
    public void setObservador(Runnable observador) {
        this.observador = observador;
    }

    /**
     * Notifica a la vista que hubo cambios. Es llamado por los hilos de las
     * garitas y por las acciones del usuario.
     */
    public void notificarCambio() {
        Runnable observadorActual = this.observador;
        if (observadorActual != null) {
            observadorActual.run();
        }
    }
}
