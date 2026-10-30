# Diagramas de clases y flujo

Representación de la implementación de `src/`, no de funciones futuras.
Se muestran los miembros principales, no todos los accesores. Los nombres de
operaciones corresponden al código. `*--` indica pertenencia; `-->`, referencia;
`..>`, uso. La relación observador se implementa con `Runnable`, sin dependencia
directa de `modelo` hacia una clase Swing.

## 1. Estructuras y dominio

```mermaid
classDiagram
    class NodoSimple {
        -Object dato
        -NodoSimple siguiente
    }
    class NodoDoble {
        -Object dato
        -NodoDoble anterior
        -NodoDoble siguiente
    }
    class ListaSimple {
        +agregar(Object dato)
        +eliminar(Object dato) boolean
        +aArreglo() Object[]
    }
    class ListaDoble {
        +agregar(Object dato)
        +eliminar(Object dato) boolean
        +aArreglo() Object[]
        +aArregloInverso() Object[]
    }
    class Cola {
        +encolar(Object dato)
        +desencolar() Object
        +aArreglo() Object[]
    }
    class Pila {
        +apilar(Object dato)
        +desapilar() Object
        +aArreglo() Object[]
    }
    class SistemaParqueo {
        +registrarResidente(nombre, casa, socio) Residente
        +registrarVehiculo(id, placa, marca, modelo, color, tipo) Vehiculo
        +solicitarEntrada(String placa)
        +solicitarSalida(String placa)
        +procesarEntrada(Vehiculo vehiculo) boolean
        +procesarSalida(Vehiculo vehiculo) boolean
        +ultimosEventos(int limite) Evento[]
        +notificarCambio()
    }
    class Residente {
        -String id
        -String nombre
        -String casa
        -boolean socio
        +agregarVehiculo(Vehiculo vehiculo)
    }
    class Vehiculo {
        -String placa
        -boolean visitante
        -EstadoVehiculo estado
        +puedeUsarAreaSocios() boolean
    }
    class Parqueo {
        +asignarEspacio(Vehiculo vehiculo) EspacioParqueo
        +liberarEspacio(String placa) Vehiculo
        +espaciosEnOrden() EspacioParqueo[]
    }
    class FilaParqueo {
        -char letra
        +obtenerEspacio(int numero) EspacioParqueo
    }
    class EspacioParqueo {
        -String id
        -TipoEspacio tipo
        +estaOcupado() boolean
    }
    class Evento {
        -TipoEvento tipo
        -String descripcion
        -String placa
        -String espacio
        +getFechaHora() String
    }
    ListaSimple *-- NodoSimple : nodos
    ListaDoble *-- NodoDoble : nodos
    Cola *-- NodoSimple : frente y fin
    Pila *-- NodoSimple : cima
    NodoSimple --> NodoSimple : siguiente
    NodoDoble --> NodoDoble : anterior y siguiente
    SistemaParqueo *-- ListaDoble : residentes
    SistemaParqueo *-- ListaSimple : visitantes
    SistemaParqueo *-- "2" Cola : entrada y salida
    SistemaParqueo *-- Pila : eventos
    SistemaParqueo *-- Parqueo
    SistemaParqueo ..> Residente : registra y busca
    Residente *-- ListaSimple : vehiculos
    Residente "1" o-- "0..3" Vehiculo
    Parqueo *-- ListaDoble : filas
    Parqueo "1" o-- "8" FilaParqueo
    FilaParqueo *-- ListaSimple : espacios
    FilaParqueo o-- EspacioParqueo : 15 o 25
    EspacioParqueo --> EspacioParqueo : arriba y abajo
    EspacioParqueo --> "0..1" Vehiculo : ocupante
    Vehiculo --> "0..1" EspacioParqueo : espacioAsignado
    Pila ..> Evento : datos del historial
```

Los objetos de dominio se guardan como `dato` de los nodos; las relaciones
de filas, vehículos y eventos resumen ese almacenamiento indirecto.

## 2. Coordinación, persistencia y vista

```mermaid
classDiagram
    class Main
    class SistemaParqueo
    class Runnable {
        <<interface>>
        +run()
    }
    class AdministradorGaritas {
        +iniciar()
        +detener()
    }
    class GaritaEntrada {
        +run()
        +estaAtendiendo() boolean
        +getVehiculoAtendido() String
    }
    class GaritaSalida {
        +run()
        +estaAtendiendo() boolean
        +getVehiculoAtendido() String
    }
    class GestorArchivos {
        +cargar(SistemaParqueo sistema) String
        +guardar(SistemaParqueo sistema)
    }
    class VentanaPrincipal {
        +actualizarTodo()
        +setAdministradorGaritas(AdministradorGaritas administrador)
    }
    class PanelResidentes
    class PanelVehiculos
    class PanelParqueo
    class PanelEntrada
    class PanelSalida
    class PanelEventos
    class Simulador
    Main ..> SistemaParqueo : crea
    Main ..> GestorArchivos : carga
    Main ..> VentanaPrincipal : crea
    Main ..> AdministradorGaritas : inicia
    AdministradorGaritas *-- "3" GaritaEntrada
    AdministradorGaritas *-- "2" GaritaSalida
    Runnable <|.. GaritaEntrada
    Runnable <|.. GaritaSalida
    GaritaEntrada --> SistemaParqueo : consume entrada
    GaritaSalida --> SistemaParqueo : consume salida
    SistemaParqueo --> Runnable : observador
    GestorArchivos ..> SistemaParqueo : consulta y reconstruye
    VentanaPrincipal --> GestorArchivos : guarda al cerrar
    VentanaPrincipal --> AdministradorGaritas : detiene al cerrar
    VentanaPrincipal *-- PanelResidentes
    VentanaPrincipal *-- PanelVehiculos
    VentanaPrincipal *-- PanelParqueo
    VentanaPrincipal *-- PanelEntrada
    VentanaPrincipal *-- PanelSalida
    VentanaPrincipal *-- PanelEventos
    VentanaPrincipal ..> Simulador
    Simulador ..> SistemaParqueo
```

Todos los paneles conservan una referencia a `SistemaParqueo`. Los paneles
Entrada y Salida también reciben el administrador para consultar las garitas.
El observador es una lambda registrada por `Main` que agenda el refresco de
la ventana mediante `SwingUtilities.invokeLater`.

## 3. Flujo de circulación

```mermaid
flowchart TD
    A[Vehiculo registrado o visitante temporal] --> B[Solicitar entrada]
    B --> C[Cola FIFO de entrada]
    C --> D[Una de tres garitas]
    D --> E{Espacio libre en su zona?}
    E -->|No| F[Rechazo en pila; vuelve a FUERA]
    F --> G[Si visitante, retirar registro temporal]
    E -->|Si| H[Asignar espacio; ingreso y asignacion en pila]
    H --> I[EN_PARQUEO]
    I --> J[Solicitar salida]
    J --> K[Cola FIFO de salida]
    K --> L[Una de dos garitas]
    L --> M[Liberar espacio; liberacion y salida en pila]
    M --> N[FUERA; retirar visitante temporal]
```

Volver al [manual técnico](../Manual_Tecnico.md).
