# Demo app architecture

This app is a **reference integration**: besides showing what HeimUI does, it shows how to wire it
up. Every decision below is meant to be copied.

```
shared/src/commonMain/kotlin/io/heimui/demo/
│
├── App.kt                          entry point
│
├── domain/                         depends on nothing
│   ├── model/                      DemoVertical, DemoTab, DemoDestination
│   ├── repository/                 DemoCatalogRepository (contract)
│   └── session/                    DemoSession (auth contract)
│
├── data/                           implements the contracts
│   ├── StaticDemoCatalogRepository compiled-in catalog + the SDUI base URL
│   └── session/                    InMemoryDemoSession, SettingsStorageDriver
│
├── presentation/
│   ├── DemoNavigationViewModel     navigation, HeimAction translation, deep links
│   ├── VerticalViewModel           selected tab + payload inspector
│   └── navigation/DemoNavHost      destination → screen
│
├── designsystem/                   LEVELS 1 and 2 — what the user sees
│   ├── DemoTheme                   the whole extension map, in one file
│   ├── MaterialHeimIconProvider    a name from the payload → a glyph of yours
│   ├── tokens/                     colours, typography, shapes, spacing
│   └── components/                 LEVEL 3 — native composables addressed by name
│
├── integration/                    LEVEL 2 — how it behaves
│   ├── DemoImageLoader             asks the CDN for the size actually drawn
│   ├── DemoUrlLauncher             claims heimui:// before it reaches the OS
│   ├── DemoModalPresenter          dialogs and sheets in the app's own shape
│   └── DemoActionInterceptors      stops a submit before it reaches the network
│
├── devtools/                       the `</>` panel
│   ├── SduiSourceInspector         the JSON behind the screen
│   ├── DemoTelemetryObserver       SDK events
│   └── TelemetryLog                how they look
│
├── di/DemoDependencies             composition root
│
└── ui/                             composables that own no state
```

## Why it is split this way

**Each folder changes for a different reason and is owned by a different person.** A designer
adjusting the palette opens `designsystem/` and never sees the navigation. Someone pointing the
demo at another backend touches `di/` and nothing else.

**`App.kt` is three lines on purpose.** It used to carry seven responsibilities: wiring, SDK
initialisation, palette, brand tokens, component registration, navigation state, and a 48-line
custom component. All of that now lives somewhere that names it.

**Composables own no state.** They read a `uiState` and send intents. That is why rotating the
phone does not lose the selected tab, and why the flow can be tested without a device.

## A ViewModel here, but not in the SDK

`heimui-core` does **not** require a ViewModel. It is a library, and forcing a
`ViewModelStoreOwner` on every consumer would impose a lifecycle dependency that is awkward on
iOS — so the SDK uses `HeimScreenController`, a plain class.

An **app** has no such constraint. Here a ViewModel is exactly right: this state must survive
configuration changes and be testable without a Compose harness.

Same reasoning, opposite conclusion, because the constraints differ. Worth understanding before
copying either one.

## Where the seam with the SDK is

**HeimUI never navigates on its own.** It dispatches `NavigateAction` and stops there, because only
the app knows its own graph. That translation lives in `DemoNavigationViewModel.onHeimAction()` —
the single most important integration point, which is why it is isolated in one function.

What the SDK **does** do before calling you: it executes `submit_form` (with form validation and
`{{state.*}}` interpolation), opens URLs under the scheme policy, and presents dialogs and bottom
sheets. The app only handles what it alone can decide.

## Screens come from the SDK's real repository

Screen ids are relative paths:

```
{baseUrl}/screens/{screenId}
  → https://raw.githubusercontent.com/heimui-io/heimui-demo/main/sdui/screens/hub/hub_screen.json
```

That means `HeimUI.initialize()` needs no networking code of our own, and it makes the demo
genuinely exercise the cache, ETag revalidation, stale-while-revalidate, timeouts and the circuit
breaker. GitHub Raw answers `304` to `If-None-Match`, so the revalidation path is tested every time
you reopen a screen.

A demo that fetches its JSON with its own `HttpClient` demonstrates none of that.

## Payloads are part of the contract

The JSON under `sdui/screens/` validates against
[`heimui-screen.schema.json`](../heimui-core/schema/). Before publishing changes:

```bash
npx ajv-cli validate -s ../heimui-core/schema/heimui-screen.schema.json \
  -d "sdui/screens/**/*.json" --spec=draft2020
```

A misspelled style does not break the app: it falls back to the default, silently. That is correct
fault tolerance at runtime, and exactly why validation belongs in CI — the schema is what turns a
silent fallback into a visible error.

## Iterating without publishing

```bash
python3 -m http.server 8080
```

Point `StaticDemoCatalogRepository.sduiBaseUrl` at `http://10.0.2.2:8080/sdui` (10.0.2.2 is the
emulator's alias for the host machine). Plain HTTP only reaches a debug build: `src/debug` carries a
network security config that exempts loopback addresses, and release builds still refuse cleartext.

## The four levels of customisation, and where to look at each

A client app does not have to choose between "use the SDK as it comes" and "fork it". There are four
levels, and each has a concrete place in this app.

| Level | What it replaces | Here | How it is verified |
|---|---|---|---|
| **1. Tokens** | How it looks | `designsystem/tokens/` | Visible on every screen |
| **2. Providers** | How it behaves | `designsystem/` and `integration/` | `IntegrationSeamsTest` + on device |
| **3. Custom components** | Your own composables | `designsystem/components/` | `stock_chart` in Storybook |
| **4. Data layer** | The networking entirely | `OfflineRepositoryTest` | Renders with no socket opened |

All of levels 1 and 2 is wired in **one file**: `designsystem/DemoTheme`. That is deliberate —
an integrator should be able to read one screenful of code and know every seam they own.

### Three of these seams have no pixels

An interceptor that refuses a submit and a launcher that claims a scheme are invisible when they
work. That is why they are pinned by tests rather than screenshots:

- `RequireSessionInterceptor` does not call `next`, so the request is **never built**. A check
  inside one screen protects that screen; in the pipeline every screen inherits it, including
  screens written after this code.
- `DemoUrlLauncher` claims `heimui://` and returns `true`. Handing it to the OS would take the user
  out of the app and bring them back through a cold start, losing the back stack on the way.
- The scheme policy is an **allow-list, not a deny-list**. `intent://` reaches unexported components
  on Android and `file://` discloses local storage; a deny-list will always miss the next scheme
  nobody thought of.

### Storage is real

`SettingsStorageDriver` used to be an in-memory map with a comment admitting it, which made both the
"persistent cache" and the form drafts decorative. It now sits on SharedPreferences (Android) and
NSUserDefaults (iOS) through `expect`/`actual`, with no dependency added.

Verified the way that counts: fill in the KYC form, kill the process with `am force-stop`, cold
start the app, and **the field comes back filled** — including in a minified R8 build.

It is not encrypted. An app whose payloads carry a balance, a name or a document number wants
EncryptedSharedPreferences or the Keychain there instead. `HeimStorageDriver` is four methods, so
that swap touches one file.
