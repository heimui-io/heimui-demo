# 📋 PROMPT MAESTRO ACTUALIZADO: DOCUMENTACIÓN OFICIAL DE HEIMUI CORE (PUBLICADO EN HEIMUI-DEMO)

Copia y pega todo el texto dentro del siguiente bloque en cualquier IA (Claude 3.5 Sonnet, ChatGPT GPT-4o, Gemini 1.5 Pro, etc.):

```text
Actúa como un Senior Technical Writer y Frontend Architect especializado en crear portales de documentación técnica de clase mundial para librerías de software (estilo Coil, Koin, Ktor, Supabase, Tailwind CSS y Stripe).

Tu misión es crear el sitio web completo de documentación técnica oficial para el SDK "HeimUI Core" (la librería de Server-Driven UI para Kotlin Multiplatform / Compose Multiplatform en Android e iOS).

Este sitio web vivirá en la carpeta docs/ del repositorio público "heimui-demo" y se desplegará gratis en GitHub Pages. Debe ser un archivo estático moderno (index.html autocontenido con Tailwind CSS por CDN, Highlight.js para sintaxis de código Kotlin/JSON, Lucide Icons y JavaScript vanilla para interactividad, búsqueda y copiado de código).

================================================================================
1. IDENTIDAD VISUAL & BRAND GUIDELINES (PALETA OFICIAL DE HEIMDALL / HEIMUI)
================================================================================
El diseño debe seguir con total fidelidad la paleta de colores del logo oficial de Heimdall (Cyberpunk Blue Heeler):
* Background Principal: #0B0F19 (Obsidian Dark del logo)
* Superficies / Cards: #161D2F (Azul profundo de las tarjetas)
* Hover de Superficies: #1E293B
* Bordes y Separadores: #334155 (Gris tecnológico)
* Acento Primario: #00E5FF (Electric Cyan - Ojos y circuitos neón del logo)
* Acento Secundario: #A855F7 (Neon Violet - Orejas y aura cibernética)
* Acento de Marca: #D97706 (Guardian Tan - Puntos en las cejas de Heimdall)
* Texto Primario: #F8FAFC (Blanco puro)
* Texto Secundario: #94A3B8 (Gris slate de lectura cómoda)
* Estados / Badges: Success #10B981, Warning #F59E0B, Error #EF4444

Tipografías:
* Inter o Plus Jakarta Sans para títulos y textos.
* JetBrains Mono o Fira Code para bloques de código.

Elementos UI Requeridos:
- Header superior con logo de HeimUI, insignia "v0.0.1-alpha", barra de búsqueda en vivo y botón a GitHub (https://github.com/heimui-io/heimui-demo).
- Sidebar izquierda fija con navegación fluida por secciones y scroll-spy.
- Bloques de código con botón "Copiar", pestañas interactivas (Kotlin DSL vs JSON Payload) y sintaxis coloreada.
- Cajas de alerta estilizadas: [NOTE], [TIP], [WARNING] y [SECURITY].

================================================================================
2. CONTENIDO TÉCNICO COMPLETO DEL SDK CORE (CÓMO IMPLEMENTARLO)
================================================================================

La documentación debe estructurar, redactar y ejemplificar con código real los siguientes capítulos:

---
CAPÍTULO 1: INTRODUCCIÓN & INSTALACIÓN RÁPIDA
---
* ¿Qué es HeimUI?: El motor moderno de Server-Driven UI (SDUI) para Kotlin Multiplatform que permite construir, desplegar y actualizar pantallas nativas en Android e iOS en tiempo real desde el backend sin pasar por los ciclos de aprobación de Google Play o App Store.
* Dependencia Gradle en Kotlin DSL (build.gradle.kts):
  ```kotlin
  repositories {
      mavenCentral()
      mavenLocal()
  }
  dependencies {
      implementation("io.heimui:heimui-core:0.0.1-alpha")
  }
  ```
* Quickstart en 3 pasos:
  1. Inicializar en Application (Android) o AppDelegate/startup (iOS):
     ```kotlin
     HeimUI.initialize(
         HeimConfig(baseUrl = "https://api.tuempresa.com/sdui")
     )
     ```
  2. Envolver la UI con el tema oficial:
     ```kotlin
     HeimTheme {
         HeimScreen(screenId = "home_screen")
     }
     ```
  3. ¡Listo! El motor descarga el JSON remoto, valida el schema, renderiza los composables nativos y administra loading, pull-to-refresh y errores automáticamente.

---
CAPÍTULO 2: ARQUITECTURA & HEIMCONFIG
---
* Opciones de configuración en HeimConfig:
  - baseUrl: URL base del backend o CDN donde residen los payloads JSON.
  - customHttpClient: Instancia personalizada de Ktor HttpClient con interceptores o SSL Pinning.
  - enableHmacValidation: Verificación criptográfica HMAC-SHA256 para evitar inyección o alteración de payloads.
  - circuitBreaker: Resiliencia automática ante caídas del servidor.
  - authTokenProvider: Lambda suspendible () -> String? para adjuntar tokens Bearer/JWT dinámicos en los headers HTTP.
* Ciclo de Vida del SDK:
  - HeimUI.initialize(config): Inicialización o reconfiguración en caliente.
  - HeimUI.reset(): Cierre y liberación de recursos al cerrar sesión (Logout).
  - HeimUI.repository: Instancia singleton del repositorio para consultas manuales.

---
CAPÍTULO 3: CATÁLOGO DE COMPONENTES SDUI (JSON SCHEMA & COMPOSE)
---
Explicar cada familia de primitivas soportadas con su schema JSON y cómo se renderiza en Compose:
1. Layout Containers:
   - "column", "row", "box", "container" (soporta background_color, corner_radius, padding, margin, border).
   - "card", "spacer" (espaciado dinámico en dp).
2. Primitivas de Contenido:
   - "text": Soporta text, style (displayLarge, titleMedium, bodySmall, etc.), color, font_weight, alignment.
   - "image": Carga asíncrona mediante Coil 3 con caché inteligente en memoria/disco y content_scale.
   - "button": Variantes filled, outlined, text, tonal con texto, iconos y acciones asociadas.
   - "badge", "divider".
3. Listas y Scroll:
   - "lazy_column", "lazy_row", "grid" con soporte de paginación infinita y skeletons de carga (HeimSkeletonRenderer).
4. Formularios y Entradas Interactivas:
   - "text_field", "switch", "checkbox", "dropdown".
   - Soporte para reglas de validación (validation_rules con regex, required, min_length).
   - Visibilidad condicional con "visible_if" (el componente se muestra u oculta reactivamente según el valor de otro campo).

---
CAPÍTULO 4: ACCIONES, NAVEGACIÓN & EVENTOS (HEIMACTION)
---
Explicar el modelo declarativo de acciones con HeimAction:
- "navigate": Navegación nativa entre pantallas pasando screen_id y params.
- "open_url": Apertura de Deep Links internos (ej. heimui://showcase/ecommerce) o URLs web externas.
- "show_bottom_sheet": Apertura de modal inferior cuyo contenido es otro componente SDUI completo.
- "show_dialog" & "show_snackbar": Alertas y notificaciones dinámicas.
- "submit_form": Serialización y envío de formularios al backend vía POST/PUT.
- Ejemplo de código en Kotlin:
  ```kotlin
  HeimScreen(
      screenId = "catalog_screen",
      onAction = { action ->
          when (action) {
              is NavigateAction -> navController.navigate(action.screenId)
              is OpenUrlAction -> uriHandler.openUri(action.url)
              is ShowBottomSheetAction -> modalState.show(action.content)
              else -> Unit
          }
      }
  )
  ```

---
CAPÍTULO 5: EXTENSIBILIDAD CON CUSTOM COMPONENTS (PLUGINS)
---
* Cómo inyectar componentes nativos creados por la app host usando LocalHeimCustomComponentRegistry:
  ```kotlin
  val customRegistry = HeimCustomComponentRegistry().apply {
      register("stock_chart") { component, stateManager, onAction, modifier ->
          StockChartWidget(
              ticker = component.data["ticker"]?.asString ?: "HEIM",
              price = component.data["price"]?.asDouble ?: 0.0
          )
      }
  }

  HeimTheme(customComponentRegistry = customRegistry) {
      HeimScreen(screenId = "dashboard")
  }
  ```
* Brand Tokens: Personalización de tokens de diseño dinámicos con HeimBrandTokens.

---
CAPÍTULO 6: GUÍA DE LA APP DEMO (5 VERTICALES DE NEGOCIO EN VIVO)
---
Explicar cómo la app de ejemplo (heimui-demo) implementa 5 casos de la industria:
1. 🛒 E-Commerce & Deals: Catálogo, badges de oferta flash y checkout interactivo en BottomSheet.
2. 💳 Fintech & Banking: Tarjeta virtual, transferencias y formulario KYC con visibilidad condicional reactiva.
3. 🍔 Food Delivery: Feed de restaurantes, carruseles de promociones y tracking de pedidos en tiempo real.
4. 💎 SaaS Paywall: Comparador de planes, selector mensual/anual y panel de cuotas de consumo.
5. ⚡ Storybook: Laboratorio de prueba de todos los componentes y tokens de diseño.

================================================================================
3. REQUISITOS TÉCNICOS DEL CÓDIGO HTML GENERADO
================================================================================
- Genera el código HTML5 completo con etiquetas semánticas (<header>, <nav>, <main>, <section>, <footer>).
- Incluye estilos Tailwind CSS limpios y consistentes con la paleta de colores obsidian/cyan/violet.
- Incluye Highlight.js cargado por CDN para que los bloques de código Kotlin y JSON tengan coloreado de sintaxis profesional.
- Agrega un buscador en tiempo real en JavaScript que filtre las secciones al escribir.
- Agrega funcionalidad para copiar código al portapapeles con feedback visual ("¡Copiado!").
- Agrega tabs interactivos funcionales para alternar entre ejemplos en Kotlin y JSON.
```
