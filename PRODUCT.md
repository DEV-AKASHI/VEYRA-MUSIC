# Product

<!-- impeccable:product-schema 1 -->

## Platform

android

## Users

Veyra Music Mobile está pensado para usuarios avanzados de música en Android que escuchan y administran su biblioteca desde el teléfono: descubren contenido, reproducen álbumes y playlists, controlan la cola, descargan música y ajustan el sonido. El mismo usuario puede continuar una sesión con Veyra Connect LAN, un receptor Chromecast, Android Auto o servicios de scrobbling.

## Product Purpose

Ofrecer un reproductor Android nativo, autónomo y de alta fidelidad para descubrir, organizar y reproducir música. La experiencia debe conservar control directo de la biblioteca, de la cola y de las transiciones, incluso en flujos offline o de red local.

## Positioning

Veyra combina reproducción móvil independiente con Smart Crossfade basado en análisis local, Best Mix, ecualización de 10 bandas, biblioteca offline y continuidad entre dispositivos mediante Veyra Connect; no es un control remoto reducido de la app de escritorio.

## Operating Context

- Uso táctil cotidiano en teléfonos Android; también debe adaptarse a tabletas, plegables, Android Auto, widgets y receptores Cast.
- Sesiones de escucha con auriculares, altavoces, coche, Chromecast y dispositivos conectados por LAN.
- Flujos relevantes: Inicio, búsqueda, biblioteca, detalle de colección, Now Playing, cola, letras, descargas, dispositivos, cuenta, ajustes e integraciones.
- El reproductor Now Playing vive como overlay sobre la navegación; la estructura de rutas y la posición de controles existentes se preservan salvo una mejora de usabilidad aprobada.

## Capabilities and Constraints

- Reproducción autónoma de música, cola, listas, álbumes, artistas, búsqueda, descargas y biblioteca local.
- Smart Crossfade, Best Mix, análisis de pista, ecualizador, letras sincronizadas y artwork animado.
- Integraciones: Veyra Connect LAN, Chromecast, Android Auto, Spotify Canvas, Qobuz, Discord, Last.fm, ListenBrainz y Supabase.
- La lógica de ExoPlayer, DSP, Smart Crossfade, C++/JNI, autenticación, autorización, red, sincronización y almacenamiento persistente queda fuera de cualquier rediseño visual.
- La UI se implementa en Kotlin y Jetpack Compose; el estado de cada pantalla debe seguir sus contratos y rutas existentes.

## Brand Commitments

- Producto: Veyra Music. Sello: AKASHI DEV. Licencia: AGPL-3.0-or-later.
- La interfaz móvil ofrece dos identidades completas, seleccionables y con la misma arquitectura de información: **Glassmorphism + Aurora UI** es la interfaz principal y reemplaza visualmente a Grove Neumorfismo; **Neo-Brutalism + Cyberpunk UI** es la segunda interfaz y reemplaza visualmente a Obsidian Slate.
- Ambas identidades deben conservar ubicaciones, rutas, acciones y conceptos de cada interfaz para que cambiar de tema no obligue a reaprender la aplicación. Se permiten mejoras de jerarquía, descubribilidad y agrupación siempre que preserven el flujo funcional.
- La identidad Veyra mantiene como base Obsidian Deep, azul eléctrico, Sky Flare, texto de alto contraste, iconografía limpia y retroalimentación táctil precisa.

## Evidence on Hand

- `README.md` y `mobile/README.md`: identidad del producto, plataforma, capacidades y restricciones.
- `mobile/android/app/src/main/java/dev/sfg/veyra/mobile/app/VeyraApp.kt`: rutas y composición de las superficies principales.
- `mobile/android/app/src/main/java/dev/sfg/veyra/mobile/ui/`: pantallas Compose, componentes, tema, vidrio, aurora, navegación adaptativa y componentes de reproducción.
- `mobile/android/app/src/main/res/`: iconos de launcher, notificación, widgets, fuentes y recursos Android existentes.
- No se han recibido capturas ni mediciones de dispositivos reales para esta fase; las decisiones de rendimiento se validarán antes de implementar cada superficie.

## Product Principles

1. La música y sus controles permanecen al alcance: cambiar el aspecto nunca cambia el significado ni la ubicación funcional de una acción.
2. La fidelidad de audio y la continuidad de reproducción tienen prioridad sobre cualquier efecto visual.
3. Las dos identidades son sistemas completos, no capas decorativas; cada una debe ser legible, coherente y reconocible por sí misma.
4. La interfaz se adapta al dispositivo y al estado real del contenido: offline, sin artwork, carga, error, cola extensa y texto ampliado.
5. Los efectos visuales deben tener una degradación segura y no excluir a quien reduce movimiento o depende de TalkBack.

## Accessibility & Inclusion

- Objetivos táctiles mínimos de 48 dp y separación mínima de 8 dp cuando haya controles independientes.
- Soporte de escala de fuente del sistema, contraste suficiente, estados de foco visibles y etiquetas accesibles para TalkBack.
- Respeto por la escala de animación/reduce motion del sistema Android.
- La información de estado nunca depende solo del color, brillo, movimiento o una carátula.
