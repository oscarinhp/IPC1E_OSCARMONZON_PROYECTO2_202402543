package persistencia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import modelo.Residente;
import modelo.SistemaParqueo;
import modelo.TipoVehiculo;
import modelo.Vehiculo;

/**
 * Guarda y carga los residentes y sus vehículos en archivos de texto plano.
 *
 * <p>Los archivos usan el separador {@code |} con el formato del enunciado:</p>
 * <pre>
 * residentes.txt:  ID|NOMBRE|CASA|SOCIO
 * vehiculos.txt:   PLACA|MARCA|MODELO|COLOR|ID_RESIDENTE
 * </pre>
 *
 * <p>Los visitantes nunca se escriben en los archivos porque son información
 * temporal. Un residente se identifica con {@code S/N} en el campo SOCIO.
 * Las líneas vacías o mal formadas se omiten con un aviso; la carga termina
 * devolviendo esos avisos para que la vista los muestre.</p>
 */
public class GestorArchivos {

    /** Nombre del archivo de residentes. */
    public static final String ARCHIVO_RESIDENTES = "residentes.txt";

    /** Nombre del archivo de vehículos. */
    public static final String ARCHIVO_VEHICULOS = "vehiculos.txt";

    private final String rutaCarpeta;

    /**
     * Crea el gestor sobre una carpeta.
     *
     * @param rutaCarpeta carpeta donde viven los archivos de datos
     */
    public GestorArchivos(String rutaCarpeta) {
        this.rutaCarpeta = rutaCarpeta;
    }

    /**
     * Guarda a los residentes y sus vehículos en los dos archivos.
     *
     * @param sistema núcleo del sistema cuyo estado se escribirá
     * @throws IOException si no se puede crear o escribir la carpeta/archivos
     */
    public void guardar(SistemaParqueo sistema) throws IOException {
        crearCarpetaSiNoExiste();

        Residente[] residentes = sistema.listarResidentes();

        Path archivoResidentes = rutaArchivo(ARCHIVO_RESIDENTES);
        try (BufferedWriter escritor = Files.newBufferedWriter(
                archivoResidentes, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (Residente residente : residentes) {
                escritor.write(lineaResidente(residente));
                escritor.newLine();
            }
        }

        Path archivoVehiculos = rutaArchivo(ARCHIVO_VEHICULOS);
        try (BufferedWriter escritor = Files.newBufferedWriter(
                archivoVehiculos, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (Residente residente : residentes) {
                for (Vehiculo vehiculo : sistema.vehiculosDeResidente(residente.getId())) {
                    escritor.write(lineaVehiculo(vehiculo, residente));
                    escritor.newLine();
                }
            }
        }
    }

    /**
     * Reconstruye las listas enlazadas del sistema leeendo los archivos.
     *
     * <p>Si un archivo no existe, la lista queda vacía y se devuelve un aviso.
     * Las líneas mal formadas u huérfanas (de un residente que no existe) se
     * omiten con un aviso y la carga continúa.</p>
     *
     * @param sistema núcleo donde se registrará lo leído
     * @return aviso final; cadena vacía cuando la carga no encontró problemas
     * @throws IOException si no se puede leer la carpeta/archivos
     */
    public String cargar(SistemaParqueo sistema) throws IOException {
        StringBuilder avisos = new StringBuilder();
        cargarResidentes(sistema, avisos);
        cargarVehiculos(sistema, avisos);
        return avisos.toString();
    }

    private void crearCarpetaSiNoExiste() throws IOException {
        Path carpeta = Paths.get(rutaCarpeta);
        if (!Files.exists(carpeta)) {
            Files.createDirectories(carpeta);
        }
    }

    private Path rutaArchivo(String nombre) {
        return Paths.get(rutaCarpeta, nombre);
    }

    private String lineaResidente(Residente residente) {
        return residente.getId() + "|" + residente.getNombre() + "|"
                + residente.getCasa() + "|"
                + (residente.isSocio() ? "S" : "N");
    }

    private String lineaVehiculo(Vehiculo vehiculo, Residente residente) {
        return vehiculo.getPlaca() + "|" + vehiculo.getMarca() + "|"
                + vehiculo.getModelo() + "|" + vehiculo.getColor() + "|"
                + residente.getId();
    }

    private void cargarResidentes(SistemaParqueo sistema, StringBuilder avisos)
            throws IOException {
        Path archivo = rutaArchivo(ARCHIVO_RESIDENTES);
        if (!Files.exists(archivo)) {
            avisos.append("No existe ").append(ARCHIVO_RESIDENTES)
                    .append("; se inicia sin residentes.\n");
            return;
        }
        int registrados = 0;
        int omitidos = 0;
        try (BufferedReader lector = Files.newBufferedReader(
                archivo, StandardCharsets.UTF_8)) {
            String linea;
            int numeroLinea = 0;
            while ((linea = lector.readLine()) != null) {
                numeroLinea++;
                if (linea.isBlank()) {
                    continue;
                }
                String[] campos = linea.split("\\|", -1);
                if (campos.length != 4) {
                    omitidos++;
                    avisos.append(ARCHIVO_RESIDENTES).append(": línea ")
                            .append(numeroLinea).append(" inválida, se omitió.\n");
                    continue;
                }
                String id = campos[0].trim();
                String nombre = campos[1].trim();
                String casa = campos[2].trim();
                boolean socio = "S".equalsIgnoreCase(campos[3].trim());
                try {
                    sistema.registrarResidenteConId(id, nombre, casa, socio);
                    registrados++;
                } catch (IllegalArgumentException e) {
                    omitidos++;
                    avisos.append(ARCHIVO_RESIDENTES).append(": línea ")
                            .append(numeroLinea).append(" no cargada (")
                            .append(e.getMessage()).append(")\n");
                }
            }
        }
        avisos.append("Residentes cargados: ").append(registrados);
        if (omitidos > 0) {
            avisos.append(" (om. ").append(omitidos).append(')');
        }
        avisos.append('\n');
    }

    private void cargarVehiculos(SistemaParqueo sistema, StringBuilder avisos)
            throws IOException {
        Path archivo = rutaArchivo(ARCHIVO_VEHICULOS);
        if (!Files.exists(archivo)) {
            avisos.append("No existe ").append(ARCHIVO_VEHICULOS)
                    .append("; se inicia sin vehículos.\n");
            return;
        }
        int registrados = 0;
        int omitidos = 0;
        try (BufferedReader lector = Files.newBufferedReader(
                archivo, StandardCharsets.UTF_8)) {
            String linea;
            int numeroLinea = 0;
            while ((linea = lector.readLine()) != null) {
                numeroLinea++;
                if (linea.isBlank()) {
                    continue;
                }
                String[] campos = linea.split("\\|", -1);
                if (campos.length != 5) {
                    omitidos++;
                    avisos.append(ARCHIVO_VEHICULOS).append(": línea ")
                            .append(numeroLinea).append(" inválida, se omitió.\n");
                    continue;
                }
                String placa = campos[0].trim();
                String marca = campos[1].trim();
                String modelo = campos[2].trim();
                String color = campos[3].trim();
                String idResidente = campos[4].trim();
                TipoVehiculo tipo = TipoVehiculo.AUTOMOVIL;
                try {
                    sistema.registrarVehiculo(idResidente, placa, marca, modelo, color, tipo);
                    registrados++;
                } catch (IllegalArgumentException e) {
                    omitidos++;
                    avisos.append(ARCHIVO_VEHICULOS).append(": línea ")
                            .append(numeroLinea).append(" no cargada (")
                            .append(e.getMessage()).append(")\n");
                }
            }
        }
        avisos.append("Vehículos cargados: ").append(registrados);
        if (omitidos > 0) {
            avisos.append(" (om. ").append(omitidos).append(')');
        }
        avisos.append('\n');
    }
}
