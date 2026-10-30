import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import estructuras.Cola;
import estructuras.ListaDoble;
import estructuras.ListaSimple;
import estructuras.Pila;
import hilos.AdministradorGaritas;
import modelo.EspacioParqueo;
import modelo.Parqueo;
import modelo.Residente;
import modelo.SistemaParqueo;
import modelo.TipoEspacio;
import modelo.TipoEvento;
import modelo.TipoVehiculo;
import modelo.Vehiculo;
import persistencia.GestorArchivos;

/** Pruebas sin dependencias, contra las clases del JAR. No escribe en datos/. */
public class VerificarEntrega {
    private static int comprobaciones;

    public static void main(String[] args) throws Exception {
        if (args.length != 2) throw new IllegalArgumentException("Indicar datos y carpeta temporal.");
        System.out.println("Java: " + System.getProperty("java.version"));
        estructuras();
        reglas();
        parqueoLleno();
        persistencia(Path.of(args[0]), Path.of(args[1]));
        concurrencia();
        System.out.println("RESULTADO: " + comprobaciones + " comprobaciones OK.");
    }

    private static void verificar(boolean condicion, String caso) {
        if (!condicion) throw new AssertionError(caso);
        comprobaciones++;
        System.out.println("OK " + caso);
    }

    private static void rechaza(Runnable accion, String caso) {
        boolean rechazado = false;
        try { accion.run(); } catch (IllegalArgumentException e) { rechazado = true; }
        verificar(rechazado, caso);
    }

    private static void estructuras() throws Exception {
        ListaDoble doble = new ListaDoble();
        doble.agregar("A"); doble.agregar("B"); doble.agregar("C");
        verificar("C".equals(doble.aArregloInverso()[0]), "Lista doble: recorrido inverso");
        doble.eliminar("B");
        verificar(doble.getInicio().getSiguiente().getAnterior() == doble.getInicio(),
                "Lista doble: enlaces tras eliminar intermedio");
        doble.eliminar("A"); doble.eliminar("C");
        verificar(doble.getInicio() == null && doble.getFin() == null, "Lista doble: vaciado");
        ListaSimple simple = new ListaSimple();
        simple.agregar("A"); simple.agregar("B"); simple.eliminar("B");
        verificar(simple.getFin() == simple.getInicio(), "Lista simple: eliminar ultimo");
        Cola cola = new Cola();
        cola.encolar("A"); cola.encolar("B");
        verificar("A".equals(cola.desencolar()) && "B".equals(cola.desencolar()), "Cola FIFO");
        Thread espera = new Thread(() -> {
            try { cola.desencolar(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });
        espera.setDaemon(true);
        espera.start();
        long limite = System.nanoTime() + 2_000_000_000L;
        while (espera.getState() != Thread.State.WAITING && System.nanoTime() < limite) Thread.sleep(5);
        verificar(espera.getState() == Thread.State.WAITING, "Cola vacia: wait sin busy-wait");
        cola.encolar("C"); espera.join(2000);
        verificar(!espera.isAlive(), "notifyAll despierta consumidor");
        Pila pila = new Pila(); pila.apilar("A"); pila.apilar("B");
        verificar("B".equals(pila.aArreglo()[0]) && pila.getTamanio() == 2, "Consulta LIFO no destructiva");
        verificar("B".equals(pila.desapilar()) && "A".equals(pila.desapilar()), "Desapilar LIFO");
    }

    private static void reglas() throws Exception {
        SistemaParqueo s = new SistemaParqueo();
        Residente r = s.registrarResidente("Socio prueba", "1", true);
        Vehiculo v = s.registrarVehiculo(r.getId(), "DOC1", "Toyota", "2020", "Rojo", TipoVehiculo.AUTOMOVIL);
        s.registrarVehiculo(r.getId(), "DOC2", "Toyota", "2020", "Rojo", TipoVehiculo.AUTOMOVIL);
        s.registrarVehiculo(r.getId(), "DOC3", "Toyota", "2020", "Rojo", TipoVehiculo.AUTOMOVIL);
        rechaza(() -> s.registrarVehiculo(r.getId(), "DOC4", "Toyota", "2020", "Rojo", TipoVehiculo.AUTOMOVIL),
                "Rechazo de cuarto vehiculo");
        rechaza(() -> s.registrarVehiculo(r.getId(), "doc1", "Toyota", "2020", "Rojo", TipoVehiculo.AUTOMOVIL),
                "Placa duplicada sin distinguir mayusculas");
        s.solicitarEntrada(v.getPlaca());
        rechaza(() -> s.solicitarEntrada(v.getPlaca()), "Rechazo de solicitud de entrada duplicada");
        rechaza(() -> s.eliminarResidente(r.getId()), "No eliminar residente con vehiculo activo");
        verificar(s.procesarEntrada(s.tomarDeColaEntrada()) && v.getEspacioAsignado().getTipo() == TipoEspacio.SOCIO,
                "Socio asignado a A-C");
        Vehiculo visitante = s.registrarVisitante("Visita", "VIS1", "Honda", "2021", "Azul", TipoVehiculo.AUTOMOVIL);
        s.solicitarEntrada(visitante.getPlaca()); s.procesarEntrada(s.tomarDeColaEntrada());
        verificar(visitante.getEspacioAsignado().getTipo() == TipoEspacio.GENERAL, "Visitante solo en E-I");
        s.solicitarSalida("VIS1"); s.procesarSalida(s.tomarDeColaSalida());
        verificar(s.buscarVehiculoPorPlaca("VIS1") == null && s.visitantesActivos().length == 0,
                "Visitante eliminado al salir");
        verificar(s.ultimosEventos(1)[0].getTipo() == TipoEvento.SALIDA, "Ultimo evento primero");
    }

    private static void parqueoLleno() {
        Parqueo p = new Parqueo();
        verificar(p.getTotalEspacios() == 150 && p.contarTotales(TipoEspacio.SOCIO) == 75
                && p.contarTotales(TipoEspacio.GENERAL) == 75, "Distribucion 150 espacios, 75 por zona");
        boolean enlaces = true;
        for (EspacioParqueo e : p.espaciosEnOrden()) {
            if (e.getAbajo() != null) enlaces &= e.getAbajo().getArriba() == e;
            if (e.getArriba() != null) enlaces &= e.getArriba().getAbajo() == e;
        }
        verificar(enlaces, "Enlaces verticales reciprocos");
        Vehiculo primero = null;
        for (int i = 0; i < 75; i++) {
            Vehiculo v = new Vehiculo("LLENO" + i, "Toyota", "2020", "Rojo", TipoVehiculo.AUTOMOVIL);
            EspacioParqueo e = p.asignarEspacio(v);
            if (e == null || e.getTipo() != TipoEspacio.GENERAL) throw new AssertionError("Zona incorrecta");
            if (i == 0) primero = v;
        }
        verificar(p.asignarEspacio(new Vehiculo("EXTRA", "Honda", "2020", "Azul", TipoVehiculo.AUTOMOVIL)) == null,
                "Zona general llena no invade socios");
        p.liberarEspacio(primero.getPlaca());
        verificar(p.contarLibres(TipoEspacio.GENERAL) == 1, "Liberacion recupera disponibilidad");
    }

    private static void persistencia(Path datos, Path temporal) throws Exception {
        SistemaParqueo original = new SistemaParqueo();
        String avisos = new GestorArchivos(datos.toString()).cargar(original);
        verificar(!avisos.contains("omit") && !avisos.contains("no cargada") && !avisos.contains("No existe")
                && original.listarResidentes().length > 0, "Carga de archivos de ejemplo");
        original.registrarVisitante("No persistir", "TEMPVIS", "Honda", "2020", "Azul", TipoVehiculo.AUTOMOVIL);
        Files.createDirectories(temporal);
        Path copia = Files.createTempDirectory(temporal, "persistencia-");
        new GestorArchivos(copia.toString()).guardar(original);
        SistemaParqueo recuperado = new SistemaParqueo();
        new GestorArchivos(copia.toString()).cargar(recuperado);
        verificar(recuperado.listarResidentes().length == original.listarResidentes().length,
                "Guardado y reconstruccion de residentes");
        verificar(recuperado.buscarVehiculoPorPlaca("TEMPVIS") == null, "Visitantes no persistidos");
        verificar(recuperado.contarEspaciosLibres(TipoEspacio.SOCIO) == 75 && recuperado.cantidadEventos() == 0,
                "Reinicio sin ocupacion ni historial");
        String texto = Files.readString(copia.resolve("vehiculos.txt"), StandardCharsets.UTF_8);
        verificar(!texto.contains("TEMPVIS"), "Archivo vehiculos excluye visita temporal");
        System.out.println("Archivos de prueba conservados en: " + copia);
    }

    private static void concurrencia() throws Exception {
        SistemaParqueo s = new SistemaParqueo();
        AdministradorGaritas admin = new AdministradorGaritas(s);
        try {
            for (int i = 0; i < 9; i++) {
                s.registrarVisitante("Concurrente " + i, "CON" + i, "Honda", "2020", "Azul", TipoVehiculo.AUTOMOVIL);
                s.solicitarEntrada("CON" + i);
            }
            admin.iniciar();
            esperar(() -> s.vehiculosEstacionados().length == 9, "Ingresos concurrentes");
            verificar(admin.getGaritasEntrada().length == 3 && admin.getGaritasSalida().length == 2,
                    "Tres garitas de entrada y dos de salida iniciadas");
            boolean unicos = true;
            Vehiculo[] estacionados = s.vehiculosEstacionados();
            for (int i = 0; i < estacionados.length; i++) {
                for (int j = i + 1; j < estacionados.length; j++) {
                    unicos &= estacionados[i].getEspacioAsignado() != estacionados[j].getEspacioAsignado();
                }
            }
            verificar(unicos && s.contarEspaciosOcupados(TipoEspacio.GENERAL) == 9,
                    "Nueve ingresos sin espacios duplicados");
            for (Vehiculo v : estacionados) s.solicitarSalida(v.getPlaca());
            esperar(() -> s.vehiculosEstacionados().length == 0, "Salidas concurrentes");
            verificar(s.visitantesActivos().length == 0 && s.contarEspaciosLibres(TipoEspacio.GENERAL) == 75,
                    "Salidas concurrentes liberan espacios y visitantes");
        } finally {
            admin.detener();
        }
    }

    private static void esperar(Condicion condicion, String caso) throws Exception {
        long limite = System.nanoTime() + 15_000_000_000L;
        while (!condicion.cumple() && System.nanoTime() < limite) Thread.sleep(20);
        verificar(condicion.cumple(), caso);
    }

    private interface Condicion { boolean cumple(); }
}
