# MEMORY.md — Veyra Music

## Identidad del proyecto

- **Producto:** Veyra Music
- **Sello:** AKASHI DEV
- **Licencia:** AGPL-3.0-or-later

## Sistema de diseño

- Tema oscuro Obsidian Slate: fondo `#0B0F19`, superficies `#131B2B` / `#1E293B`, acento `#1E6CEB` y brillo `#38BDF8`.
- Tema claro Clean Canvas Slate: fondo `#F8FAFC`, superficies `#FFFFFF`, borde `#E2E8F0`.
- Pautas: squircles, bordes hairline, glassmorphism sutil, dock flotante e indicadores de estado compactos.

## Arquitectura y persistencia

- Renderer en Vue 3; proceso de escritorio en Electron; cliente Android y módulos compartidos.
- Persistencia local con almacenamiento `orchard:*` y rutas `userData`; servicios de nube con Supabase y Workers.
- La internacionalización del renderer vive en `src/app/i18n/`, con diccionarios inglés y español de México y selector de idioma.

## Registro de trabajo

### Dirección móvil: Aurora y Cyberpunk

- Se estableció `PRODUCT.md` como fuente de verdad funcional para Veyra Music Mobile y `DESIGN.md` como contrato del rediseño.
- Glassmorphism + Aurora UI reemplaza visualmente a Grove Neumorfismo como tema principal seleccionable; Neo-Brutalism + Cyberpunk UI reemplaza a Obsidian Slate como segundo tema global seleccionable.
- Ambos temas conservan rutas, ubicación de controles y flujos existentes. La documentación define matriz de superficies, tokens semánticos, primitivas, accesibilidad y presupuesto de rendimiento para las siguientes fases.

### Estabilidad de Chromecast

- Se habilitaron el servicio de reconexión y la reanudación de sesiones guardadas de Google Cast.
- La app mantiene la reproducción en el receptor durante 15 segundos ante una indisponibilidad temporal de sesión antes de restaurarla al teléfono; así evita que microcortes de Wi‑Fi finalicen la reproducción remota.

### Skills de diseño prioritarias

- Se instalaron localmente `impeccable` y `design-taste-frontend` en `.agents/skills/`.
- Para toda tarea de interfaz, `impeccable` es el flujo prioritario. En páginas web de presentación o marketing se complementa obligatoriamente con `design-taste-frontend`, respetando el sistema visual existente de Veyra Music.

### Internacionalización — textos faltantes

- Se migraron textos visibles que aún estaban escritos directamente en inglés a claves de traducción, incluyendo soporte, reproductor, biblioteca, búsqueda, ajustes, diálogos, cola, navegación, cuenta, acciones de reproducción y mensajes de diagnóstico/análisis.
- Se añadieron las traducciones correspondientes en inglés y español de México.
- Se conectaron también cadenas dinámicas emitidas por módulos de reproducción y acciones para que respeten el idioma seleccionado.

### Cliente Móvil v1.0.2 (Efecto Cristal, Auto-reproducción Offline y Claridad de Cuenta)

- **Versión oficial:** `1.0.2` (código de versión 3, codename "AKASHI DEV").
- **Efecto Cristal Esmerilado Universal:**
  - Shader neo-glassmórfico de alto rendimiento con desenfoque de arte reactivo en tiempo real y soporte visual transversal.
- **Claridad UI/UX para Cuenta de Veyra (Cloud Sync):**
  - Switcher explícito entre "Iniciar sesión" y "Crear cuenta".
  - Mensajes descriptivos en español e inglés explicando que la cuenta es opcional y sirve para sincronizar biblioteca y DSP entre el celular y la app de escritorio de PC.
- **Auto-reproducción Continua de Descargas Offline:**
  - La cola de descargas ahora resuelve directamente los IDs locales sin requerir conexión a internet ni bloquear el auto-avance, permitiendo reproducción ininterrumpida de todas las canciones descargadas.
- **Changelog Bilingüe Dinámico:**
  - Diálogo de novedades con detección reactiva de idioma (español e inglés) tras cada actualización.

- **Versión oficial:** `1.0.0` (código de versión 1, codename "AKASHI DEV", namespace `dev.sfg.veyra.connect`).
- **Autenticación e Integraciones:**
  - Login webview nativo con cookies aisladas para Spotify (extracción de `sp_dc` y ClientToken) y Qobuz.
  - Sincronización de nube conectada a la instancia oficial de Supabase de Veyra Music (`furkfhzvvgxlyxukqomm.supabase.co`).
  - Auto-migración en `SharedPreferences` para descartar URLs heredadas de Orchard y redirigir a Veyra.
  - Actualizador móvil vinculado al repositorio oficial de GitHub `SCARLXRD-1/VEYRA-MUSIC`.
- **UI/UX y Paradigmas de Reproducción:**
  - **Grove (Dock Inferior):** Isla flotante squircle con relieve neumórfico, aura reactiva al acento del tema y mini-scrubber con onda sinusoidal activa continua.
  - **Obsidian (HUD Superior):** Consola estilo rack de estudio con vúmetro tri-banda LED en tiempo real, filamento láser osciloscopio y píldora de telemetría WSOLA DSP.
  - **Now Playing:** Scrubber de alta fidelidad con onda sinusoidal elástica (envolvente Tukey sin tirones) y botones hero táctiles.
  - **Fondo dinámico:** Proyección ambiental difuminada de la portada de la canción a pantalla completa con doble orbe lumínico orbital y velo Obsidian contrastado.
- **Optimizaciones de Rendimiento:**
  - Reutilización de objetos `Path` en Compose Canvas para eliminar allocations de memoria por cuadro a 120 FPS en el scrubber, Grove dock, Obsidian HUD y Equalizer Sheet.
  - Gestión proactiva de memoria en Coil ImageLoader con `onTrimMemory` y `onLowMemory` para evitar cierres en segundo plano por el LMK de Android.
  - Minificación R8 y optimización ProGuard activada para APK de producción.

## Hoja de ruta

### 1. Plan de Optimización para la Versión TV (Android TV / Leanback)
- **Diagnóstico del Error en Smart TV ("No se ha podido analizar el paquete" / `INSTALL_PARSE_FAILED`):**
  - **Firma del APK (APK Signature Schemes):** Los instaladores de Android TV 8/9/10/11 exigen compatibilidad estricta con firma v1 (JAR signing) además de v2/v3. Sin v1 o con claves no alineadas mediante `zipalign`, el `PackageParser` de AOSP rechaza el paquete con error de análisis.
  - **Arquitectura de CPU (ABI 32-bit `armeabi-v7a`):** La mayoría de Smart TVs y TV Sticks (Xiaomi, TCL, Fire TV, Hisense) operan con userland de 32 bits (`armeabi-v7a`). Cualquier APK distribuido para TV debe incluir obligatoriamente binarios nativos compilados para `armeabi-v7a` (Earmark Rust, C++ resampler, Opus).
  - **Atributos de manifiesto XML y `compileSdk`:** Atributos como `android:dataExtractionRules` o recursos generados bajo `compileSdk 37` causan excepciones de análisis en versiones previas de Android TV. Se debe mantener `minSdk = 24` con retrocompatibilidad XML.
  - **Descargas truncadas:** Las herramientas comunes de TV (como Downloader) son propensas a truncar descargas de archivos APK pesados. Se implementará verificación de tamaño y SHA-256 en el portal de descargas.
- **Acciones Técnicas para Versión TV:**
  - Compilación específica para TV con firma híbrida (`v1SigningEnabled true`, `v2SigningEnabled true`), alineación `zipalign -p 4` y empaquetado para `armeabi-v7a` y `arm64-v8a`.
  - Manifiesto TV completo con `<category android:name="android.intent.category.LEANBACK_LAUNCHER" />`, banner TV (`android:banner="@drawable/ic_tv_banner"`), y `android.hardware.touchscreen = false`.
- **Navegación D-Pad / Control Remoto:** Enfoque visual neumórfico con anillo de brillo cian (`#38BDF8`), escala reactiva en tarjetas de música y navegación accesible a 3 metros (10-foot UI).
- **Now Playing para Gran Formato:** Carátula en gran escala, fondo ambiental dinámico optimizado para paneles OLED/LED 4K, letras sincronizadas con fuentes legibles a distancia.
- **Optimización de Recursos en Smart TV:** Reducción de resolución de shaders/desenfoques para procesadores de gama de entrada, limitando consumo de RAM a menos de 150 MB y fijando animación a 60 FPS estables.

### 2. Plan para Terminar de Pulir la Versión Desktop
- **Adopción Neumórfica & Obsidian Industrial:** Rediseño de componentes restantes (`HomeView.vue`, `media-card`, `shelf-section`, `EqualizerDialog.vue`) hacia squircles, bordes hairline y paleta Obsidian Slate.
- **Dock de Navegación Flotante:** Barra con radio de `26px`, glassmorphism y micro-indicadores `StatusPillBadge`.
- **Persistencia & Cloud Sync:** Sincronización continua de DSP y metadatos de análisis de audio con Supabase Cloud y SQLite local.

### 3. Rebranding Integral del Instalador de Escritorio (Desktop EXE / veyra-packages) [COMPLETADO]
- **Diagnóstico y solución:**
  - El instalador ejecutable de Windows (`veyramusic.exe`) y los paquetes de escritorio consultaban el repositorio upstream `sfg5453/orchard`.
  - Se completó la migración integral y limpieza 100% exclusiva hacia `SCARLXRD-1/VEYRA-MUSIC` y la identidad de **Veyra Music • AKASHI DEV**:
    - `veyra-packages/neutralino.config.json`: ID de app `dev.akashi.veyra.packages`, nombre "Veyra Packages", título de ventana "Veyra Music", icono oficial, autor "AKASHI DEV", binaryName `veyra-packages`, extension ID `dev.akashi.veyra.packages.backend`.
    - `veyra-packages/src/frontend/index.html` y `app.ts`: Título "Veyra Music", cintillo "AKASHI DEV · PACKAGE SERVICE", marca gráfica `veyra-packages.png`, textos y botones de instalación "Veyra Music".
    - `veyra-packages/src/backend/manifest.ts` y `paths.ts`: Descargas apuntadas a releases de Veyra Music y carpetas de configuración limpias en `%APPDATA%\veyra` y `~/.local/share/veyra`.
    - `veyra-packages/backend/types.go`: Constantes de API y descargas redirigidas a `https://github.com/SCARLXRD-1/VEYRA-MUSIC/releases/download/` y `https://api.github.com/repos/SCARLXRD-1/VEYRA-MUSIC/releases`.
    - `veyra-packages/backend/installer.go` y `runtime.go`: Descubrimiento de `manifest.json` en releases de Veyra Music, User-Agent `Veyra-Music-Packages`, staging `.veyra.staging-`, escritura exclusiva de lanzadores `veyra.cmd` (Windows) y `veyra` (Linux/macOS).
    - `veyra-packages/backend/opener.go`, `uninstaller.go` y `backend_test.go`: Sincronizados con las rutas y ejecutables de Veyra Music.
    - `veyra-packages/scripts/build-backend.ts` y `build-release.ts`: Compilación exclusiva del binario `veyra-packages-backend` y generación de bundles oficiales `veyra-packages-...`.
    - `packaging/windows/veyra.nsi` y `build-windows-installer.ps1`: BrandingText `Veyra Music • AKASHI DEV`, instaladores `Veyra-Setup-$version.exe`, instalación limpia únicamente de `veyra.cmd`.
    - `packaging/linux/common/veyra-packages.sh.in` y `build-linux-packages.sh`: Ubicación de librerías en `/usr/lib/veyra-packages/`, enlaces `/usr/bin/veyra` y `/usr/bin/veyra-packages`, paquetes deb/rpm `veyra-packages`.
    - `.github/workflows/release.yml`: Publicación exclusiva de paquetes `veyra-packages-*` (.zip, .tar.zst), instaladores de Windows `Veyra-Setup-*.exe` y sincronización con Cloudflare R2 sin duplicados legacy.
    - Única compatibilidad heredada conservada: lectura pasiva de claves `orchard:*` en `localStorage` del cliente para no borrar datos locales o favoritos previos.

### 4. Sistema Keep-Alive de Supabase (Señales cada 4 días) [COMPLETADO]
- **Objetivo:** Prevenir que la instancia oficial de Supabase (`furkfhzvvgxlyxukqomm.supabase.co`) se suspenda por inactividad (el plan gratuito se pausa a los 7 días sin peticiones).
- **Implementación multinivel:**
  1. **Frontend / Desktop Client (`src/services/supabaseClient.js` y `src/app/core/lifecycle.js`):**
     - Método `supabaseClient.keepAlive(force = false)` que consulta `localStorage.getItem('veyra_supabase_last_ping')`.
     - Si han pasado más de 4 días (o en force), ejecuta una consulta ultraligera `GET /rest/v1/track_analysis?select=video_id&limit=1` con headers de autenticación apikey y actualiza la marca de tiempo.
     - `startKeepAliveScheduler()` se invoca en `onMounted` del ciclo de vida de la app y programa un chequeo diario (cada 24 horas).
  2. **Cliente Android (`mobile/.../SupabaseSyncService.kt` y `VeyraViewModel.kt`):**
     - Método `keepAlivePing(force = false)` con umbral de 4 días respaldado en `SharedPreferences`.
     - Disparado en corrutina asíncrona en `init` de `VeyraViewModel`.
  3. **GitHub Actions Standing Cron (`.github/workflows/supabase-keepalive.yml`):**
     - Flujo programado con cron `0 12 */4 * *` (cada 4 días a las 12:00 UTC) que realiza un ping HTTP a Supabase. Garantiza actividad constante incluso si no se abre la aplicación durante semanas o meses.
  4. **Suite de Pruebas (`test/supabaseKeepAlive.test.js`):**
     - Pruebas unitarias completas que validan el salto cuando está dentro de los 4 días, ejecución tras superar el umbral, forzado manual y manejo tolerante a fallos de red.

### 5. Estructura de Releases (`releases/`)
- `releases/mobile/`: Especificaciones del APK de producción v1.0.0 (`app-release.apk`), min SDK 31+, arquitecturas `arm64-v8a`/`x86_64` y checksum SHA-256.
- `releases/desktop/`: Binarios para Linux (`veyra-v1.0.3-linux-x64.tar.gz`), Windows (`Veyra-Setup-1.0.3.exe`, `veyramusic.exe`, `veyra-v1.0.3-windows-x64.zip`) y macOS.
- `releases/tv/`: Binarios y manifiestos para Android TV / Leanback (D-Pad navigation).

> [!NOTE]
> **Estado de la sesión:** Todo el trabajo permanece 100% en local para revisión y pruebas del usuario con el instalador EXE (`Veyra-Setup-1.0.3.exe`). Pendiente cuando el usuario lo indique: revisión/depuración de releases antiguos y sincronización con el repositorio remoto.
