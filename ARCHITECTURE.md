# Arquitectura de la app demo

Esta app es una **implementación de referencia**: además de mostrar qué hace HeimUI, muestra cómo
integrarlo. Las decisiones de abajo están tomadas para que un dev pueda copiarlas.

```
shared/src/commonMain/kotlin/io/heimui/demo/
│
├── App.kt                          entry point
│
├── domain/                         no depende de nada
│   ├── model/                      DemoVertical, DemoTab, DemoDestination
│   ├── repository/                 DemoCatalogRepository (contrato)
│   └── session/                    DemoSession (contrato de auth)
│
├── data/                           implementa los contratos
│   ├── StaticDemoCatalogRepository catálogo compilado + baseUrl del SDUI
│   └── session/                    InMemoryDemoSession, SettingsStorageDriver
│
├── presentation/
│   ├── DemoNavigationViewModel     navegación + traducción de HeimAction + deep links
│   ├── VerticalViewModel           tab seleccionado + visor de payload
│   └── navigation/DemoNavHost      destino → pantalla
│
├── designsystem/                   NIVEL 1 y 2 — qué ve el usuario
│   ├── DemoTheme                   el mapa completo de extensión, en un archivo
│   ├── MaterialHeimIconProvider    un nombre del payload → un glifo tuyo
│   ├── tokens/                     colores, tipografía, formas, espaciado
│   └── components/                 NIVEL 3 — composables nativos por nombre
│
├── integration/                    NIVEL 2 — cómo se comporta
│   ├── DemoImageLoader             pide a la CDN el tamaño que se dibuja
│   ├── DemoUrlLauncher             reclama heimui:// antes de salir al SO
│   ├── DemoModalPresenter          diálogos y sheets con la forma de la app
│   └── DemoActionInterceptors      corta un submit antes de que salga a la red
│
├── devtools/                       el panel `</>`
│   ├── SduiSourceInspector         el JSON detrás de la pantalla
│   ├── DemoTelemetryObserver       eventos del SDK
│   └── TelemetryLog                cómo se ven
│
├── di/DemoDependencies             composition root
│
└── ui/                             composables sin estado propio
```


## Por qué así

**Cada carpeta cambia por una razón distinta y la toca una persona distinta.** Un diseñador que
ajusta la paleta abre `designsystem/` y nunca ve la navegación. Alguien que apunta la demo a otro
backend toca `di/` y nada más.

**`App.kt` tiene tres líneas a propósito.** Antes concentraba siete responsabilidades: wiring,
inicialización del SDK, paleta, brand tokens, registro de componentes, estado de navegación y un
componente custom de 48 líneas. Todo eso ahora vive donde se llama por su nombre.

**Los composables no tienen estado propio.** Leen un `uiState` y llaman intents. Por eso rotar el
teléfono no pierde la pestaña seleccionada, y por eso el flujo se puede testear sin dispositivo.

## ViewModel aquí, pero no en el SDK

`heimui-core` **no** exige ViewModel: es una librería, y obligar a un `ViewModelStoreOwner` le
impondría una dependencia de lifecycle a todos sus consumidores, cosa incómoda en iOS. Por eso el
SDK usa `HeimScreenController`, una clase plana.

Una **app** no tiene esa restricción. Aquí ViewModel es exactamente lo correcto: el estado debe
sobrevivir cambios de configuración y ser testeable sin un harness de Compose.

Mismo razonamiento, conclusión opuesta, porque las restricciones son distintas. Vale la pena
entender por qué antes de copiar cualquiera de los dos.

## Dónde está la costura con el SDK

**HeimUI nunca navega solo.** Despacha `NavigateAction` y ahí se detiene, porque solo la app
conoce su propio grafo. Esa traducción vive en `DemoNavigationViewModel.onHeimAction()` — es el
punto de integración más importante y por eso está aislado en una sola función.

Lo que el SDK **sí** hace antes de llamarte: ejecuta `submit_form` (con validación de formulario e
interpolación de `{{state.*}}`), abre URLs bajo política de esquemas, y presenta diálogos y bottom
sheets. La app solo maneja lo que únicamente ella puede decidir.

## Las pantallas vienen del repositorio real del SDK

Los ids de pantalla son rutas relativas:

```
{baseUrl}/screens/{screenId}
  → https://raw.githubusercontent.com/heimui-io/heimui-demo/main/sdui/screens/hub/hub_screen.json
```

Eso permite usar `HeimUI.initialize()` sin código de red propio, y hace que la demo ejercite de
verdad la caché, la revalidación por ETag, el stale-while-revalidate, los timeouts y el circuit
breaker. GitHub raw responde `304` ante `If-None-Match`, así que la ruta de revalidación se prueba
sola cada vez que reabres una pantalla.

Una demo que trae el JSON con su propio `HttpClient` no demuestra nada de eso.

## Los payloads son parte del contrato

Los JSON bajo `sdui/screens/` se validan contra
[`heimui-screen.schema.json`](../heimui-core/schema/). Antes de publicar cambios:

```bash
npx ajv-cli validate -s ../heimui-core/schema/heimui-screen.schema.json \
  -d "sdui/screens/**/*.json" --spec=draft2020
```

Un estilo mal escrito no rompe la app: cae al valor por defecto en silencio. Eso es tolerancia a
fallos correcta en runtime, y precisamente por eso hace falta validar en CI — el schema es lo que
convierte un fallo silencioso en un error visible.

## Para iterar sin publicar

```bash
cd sdui && python3 -m http.server 8080
```

Cambia `StaticDemoCatalogRepository.sduiBaseUrl` a `http://10.0.2.2:8080` (emulador Android) y
habilita cleartext en el manifest mientras pruebas.


## Los cuatro niveles de personalización, y dónde mirarlos

Una app cliente no tiene que elegir entre "usar el SDK como viene" y "forkearlo". Hay cuatro
niveles, y cada uno tiene un lugar concreto en esta app.

| Nivel | Qué reemplaza | Aquí | Cómo está verificado |
|---|---|---|---|
| **1. Tokens** | Cómo se ve | `designsystem/tokens/` | Se ve en cada pantalla |
| **2. Providers** | Cómo se comporta | `designsystem/` e `integration/` | `IntegrationSeamsTest` + emulador |
| **3. Custom components** | Composables propios | `designsystem/components/` | `stock_chart` en Storybook |
| **4. Data layer** | El networking entero | `OfflineRepositoryTest` | Renderiza sin abrir un socket |

Todo el nivel 1 y 2 se cablea en **un solo archivo**: `designsystem/DemoTheme`. Es a propósito —
quien integre debería poder leer una pantalla de código y conocer todas sus costuras.

### Tres de estas costuras no tienen píxeles

Un interceptor que rechaza un submit y un launcher que reclama un esquema son invisibles cuando
funcionan. Por eso están fijados con tests y no con capturas:

- `RequireSessionInterceptor` no llama a `next`, así que el request **nunca se construye**. Un
  check dentro de una pantalla solo protege esa pantalla; en el pipeline lo heredan todas,
  incluidas las que se escriban después.
- `DemoUrlLauncher` reclama `heimui://` y devuelve `true`. Entregárselo al SO sacaría al usuario
  de la app y lo haría volver por un cold start, perdiendo el back stack en el camino.
- La política de esquemas es **allow-list, no deny-list**. `intent://` alcanza componentes no
  exportados en Android y `file://` expone almacenamiento local; una deny-list siempre se va a
  perder el siguiente esquema que nadie pensó.

### El storage es real

`SettingsStorageDriver` era un mapa en memoria con un comentario admitiéndolo — lo que hacía
decorativos tanto el "cache persistente" como los borradores de formulario. Ahora está sobre
SharedPreferences (Android) y NSUserDefaults (iOS) vía `expect`/`actual`, sin agregar dependencias.

Verificado de la forma que importa: se llena el KYC, se mata el proceso con `am force-stop`, la
app arranca en frío y **el campo vuelve lleno** — también en build minificado con R8.

No está cifrado. Una app cuyos payloads carguen un saldo, un nombre o un documento quiere
EncryptedSharedPreferences o el Keychain ahí. `HeimStorageDriver` son cuatro métodos, así que ese
cambio toca un archivo.
