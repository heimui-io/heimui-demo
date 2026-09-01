# Arquitectura de la app demo

Esta app es una **implementación de referencia**: además de mostrar qué hace HeimUI, muestra cómo
integrarlo. Las decisiones de abajo están tomadas para que un dev pueda copiarlas.

```
shared/src/commonMain/kotlin/io/heimui/demo/
│
├── App.kt                          entry point (3 líneas)
│
├── domain/                         no depende de nada
│   ├── model/                      DemoVertical, DemoTab, DemoDestination
│   └── repository/                 DemoCatalogRepository (contrato)
│
├── data/                           implementa el contrato
│   ├── StaticDemoCatalogRepository catálogo compilado + baseUrl del SDUI
│   └── RawSduiFetcher              trae el JSON crudo para el visor de código
│
├── presentation/
│   ├── DemoNavigationViewModel     navegación + traducción de HeimAction
│   ├── VerticalViewModel           tab seleccionado + visor de payload
│   └── navigation/DemoNavHost      destino → pantalla
│
├── designsystem/
│   ├── DemoTheme                   paleta, brand tokens, registro de componentes
│   └── components/                 componentes nativos que el payload invoca por nombre
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
