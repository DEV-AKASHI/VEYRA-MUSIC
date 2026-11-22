# Veyra Music Mobile Design System

<!-- impeccable:design-schema 1 -->

## Scope and authority

Este documento define la modernización visual del cliente Android. Se aplica a las pantallas y componentes Compose bajo `mobile/android/app/src/main/java/dev/sfg/veyra/mobile/ui/` y a la integración visual del sistema Android. `PRODUCT.md` mantiene la verdad de producto; este archivo no cambia capacidades, rutas, persistencia ni el motor de audio.

La estructura funcional existente es la autoridad: Inicio, Búsqueda, Biblioteca, Descargas, Ajustes, Detalle, Dispositivos y Now Playing conservan rutas, acciones y ubicación relativa. Cualquier propuesta de reordenamiento debe mejorar descubribilidad sin alterar el significado de controles ni romper memoria muscular.

## Theme model

Veyra Mobile ofrece dos temas globales completos, elegibles desde Ajustes. El selector sustituirá el paradigma actual Grove/Obsidian sin crear un tercer modo ambiguo.

| Tema seleccionado | Evolución de | Identidad | Uso |
| --- | --- | --- | --- |
| `AURORA` | Grove Neumorfismo | Glassmorphism + Aurora UI | Tema principal y recomendado; superficies translúcidas, luz ambiental derivada del artwork y tactilidad suave. |
| `CYBERPUNK` | Obsidian Slate | Neo-Brutalism + Cyberpunk UI | Tema secundario completo; estructura firme, contraste alto, contornos duros y acentos luminosos con uso controlado. |

Cambiar de tema transforma color, elevación, superficie, borde, forma, microinteracción y tratamiento de artwork; no reubica navegación, botones de reproducción, cola, resultados, ajustes ni acciones destructivas.

## Information architecture contract

### Preserved locations

- Navegación principal: Inicio, Búsqueda, Biblioteca y Ajustes mantienen su posición de nivel superior; Descargas y Dispositivos siguen siendo destinos accesibles desde sus entradas actuales.
- Now Playing continúa siendo un overlay, no una ruta que destruya la pila de navegación.
- La cola permanece contextual al reproductor; las acciones de pista continúan en sus menús/contextos actuales.
- En teléfono compacto se usa barra de navegación Material; en ancho expandido, rail o layout plegable. La apariencia cambia por tema, la adaptación por tamaño no.

### Proposed usability improvements

Estas propuestas deben validarse al implementar cada pantalla, no se consideran cambios ya aprobados de comportamiento:

1. Mostrar el estado de reproducción y el acceso a Now Playing de forma persistente, visible y táctilmente amplia sobre las vistas de exploración.
2. Unificar encabezados de pantalla: contexto, acción principal y estado de red/carga deben ocupar una jerarquía consistente, sin duplicar títulos.
3. Hacer que filtros, descargas, dispositivos y acciones de colección comuniquen estado y consecuencia antes de abrir un panel secundario.
4. Agrupar Ajustes por intención (Cuenta y nube; Reproducción y audio; Biblioteca y descargas; Conexiones e integraciones; Apariencia) sin modificar la función de cada control.
5. Usar estados vacíos, carga y error como parte de cada pantalla en vez de ocultarlos detrás de un spinner o una ausencia de contenido.

## Surface matrix

| Superficie | Aurora | Cyberpunk | Restricción compartida |
| --- | --- | --- | --- |
| Inicio, Búsqueda y Biblioteca | Canvas Obsidian con capas aurora sutiles y cards de vidrio. | Canvas oscuro plano, paneles angulares, bordes de alto contraste y acento de sección. | Las listas y sus jerarquías no cambian de orden. |
| Detalle de álbum, playlist y artista | Artwork y halo ambiental; acciones sobre vidrio legible. | Artwork en marco fuerte; datos y acciones en bloques diferenciados. | El artwork nunca reduce la legibilidad de texto o controles. |
| Now Playing, MiniPlayer y cola | Fondo de artwork con velo Obsidian, onda viva y hero suave. | Fondo oscuro con retícula/contraste controlado, onda luminosa y controles de lectura inmediata. | Transporte, posición, volumen y cola conservan significado y posición relativa. |
| Ajustes, cuenta, login, descargas e integraciones | Superficies sobrias de vidrio; ornamento mínimo. | Filas y grupos con paneles fuertes; neón solo como estado/acción. | Acciones sensibles y formularios priorizan claridad sobre expresión. |
| Dispositivos, Connect y Chromecast | Estado de conexión en pills y superficies translúcidas. | Estado de conexión en badges de alto contraste y paneles segmentados. | Estado, error y desconexión deben poder entenderse sin color. |
| Widgets, notificación y Android Auto | Identidad compacta Obsidian/Aurora sin blur obligatorio. | Identidad Cyberpunk simplificada, sin efectos caros. | Respeta las convenciones y limitaciones del sistema Android. |
| Errores, permisos, diálogos y acciones destructivas | Material de alta legibilidad y tono contenido. | Contraste alto, sin estética de alarma permanente. | Componentes Material, foco, TalkBack y texto claro son obligatorios. |

## Visual world: Aurora

Aurora expresa profundidad tranquila y musical. El canvas se mantiene Obsidian; la luz aparece como una atmósfera procedente del artwork o del acento activo, nunca como un fondo genérico que compite con la biblioteca.

- Superficies: vidrio esmerilado con borde hairline, opacidad semántica y fallback sólido.
- Forma: squircle suave; `10–14 dp` para cards, `24 dp` para destacados y `26 dp` para dock flotante.
- Color: `#0B0F19` como canvas, `#131B2B`/`#1E293B` como superficies, azul eléctrico `#1E6CEB` y Sky Flare `#38BDF8` como acción y foco.
- Artwork: halo difuminado, orbes lentos y velo de contraste; no se usan gradientes decorativos en cada card.
- Movimiento: entrada breve, presión táctil y deriva ambiental lenta; reduce motion detiene deriva, ondas no esenciales y transiciones prolongadas.

## Visual world: Cyberpunk

Cyberpunk expresa precisión energética, no ruido permanente. Neo-brutalism aporta bloques claros, contornos y sombras duras; Cyberpunk aporta acento eléctrico restringido y una lectura de estado inmediata.

- Superficies: canvas casi negro, paneles planos con borde de 2 dp, sombras desplazadas compactas y esquinas de aproximadamente 6 dp.
- Color: cyan es la acción/selección principal; magenta, lima y ámbar distinguen categorías o estados no críticos. Ningún acento reemplaza texto, icono o etiqueta de estado.
- Tipo: la escala Material conserva legibilidad; los títulos pueden tener mayor peso y espaciado medido, mientras controles y metadatos mantienen la fuente de sistema/inter existente.
- Artwork: marco de alto contraste y overlays mínimos; no aplicar glitch, parpadeo o scanlines que oculten portada, letras o controles.
- Movimiento: feedback corto de borde, elevación o desplazamiento; no hay flicker, loops agresivos ni movimiento automático de lectura.

## Semantic token contract

Los componentes consumen roles, no hexadecimales. Los nombres siguientes son el contrato de Fase 2; la implementación puede adaptar los tokens existentes `VeyraSemantic`, `VeyraCyber`, `VeyraSpace`, `VeyraRadius`, `VeyraSize` y `VeyraMotion` sin duplicar escalas.

| Grupo | Roles obligatorios | Propósito |
| --- | --- | --- |
| Canvas | `canvas`, `canvasAlt`, `scrim`, `backdrop` | Fondo de pantalla, overlay y degradación segura. |
| Superficie | `surface`, `surfaceRaised`, `surfacePressed`, `glass`, `panel` | Contenedores y respuesta de interacción. |
| Contorno | `border`, `borderStrong`, `focusRing`, `hardBorder` | Separación, foco y firma Cyberpunk. |
| Contenido | `textPrimary`, `textSecondary`, `textTertiary`, `iconPrimary`, `iconMuted` | Jerarquía textual e iconográfica. |
| Acción | `action`, `actionPressed`, `onAction`, `focus` | Acciones primarias y foco. |
| Estado | `success`, `warning`, `error`, `info`, `offline`, `connecting` | Estado con texto/icono complementario. |
| Artwork | `artworkAccent`, `artworkDeep`, `artworkVeil` | Ambiente reactivo sin filtrar colores en la lógica de pantalla. |

## Component contract

Las primitivas compartidas que el siguiente agente debe normalizar son:

- `VeyraTheme(variant)`: provee esquema Material, roles semánticos, acento, reduce motion y forma apropiada al tema.
- `AuroraBackground`: fondo de pantalla/Now Playing; acepta accent y un fallback, no inicia animación propia cuando reduce motion está activo.
- `GlassSurface`: superficie Aurora; admite niveles de elevación semánticos y fallback sólido.
- `CyberPanel`: superficie Cyberpunk; define borde duro, sombra compacta y estados selected/pressed/focused sin usar neón indiscriminado.
- `StatusPillBadge`: estado textual, icono opcional y punto de color redundante; nunca solo un punto luminoso.
- `VeyraButton` / `VeyraIconButton`: variantes Material con área táctil real de 48 dp, feedback de tema y content description.
- `MediaCard`, `ListRow`, `SectionHeader`, `EmptyState`, `LoadState`, `ConfirmSheet`: contratos visuales comunes que sustituyen variantes ad hoc.

Estados mínimos de toda primitiva interactiva: `default`, `pressed`, `focused`, `selected`, `disabled`, `loading`, `error` cuando aplica. Los agentes no deben crear escalas locales de padding, radio, sombra, color o duración.

## Accessibility and adaptive behavior

- Material 3 gobierna navegación, gestos Back, diálogos, bottom sheets, snackbars, top app bars y comportamiento de pantallas compactas/expandidas.
- Objetivo táctil mínimo de 48 × 48 dp y separación de 8 dp entre destinos independientes.
- Tipografía mediante roles Material y `sp`; probar como mínimo con escala de fuente 1.3.
- TalkBack: todo icono accionable tiene nombre; artwork decorativo tiene descripción nula; estado de conexión, descarga y reproducción se anuncia con texto.
- Reduce motion: se consulta una única fuente de tema. Las transiciones se reducen a corte/fade breve, aurora se vuelve estática y la onda de reproducción se estabiliza cuando corresponde.
- Contraste: texto, iconos y focus ring se validan sobre los fondos más claros de artwork y sobre cada color Cyberpunk. No se aprueba texto solo por tener glow.

## Performance budget and fallbacks

- Un fondo Aurora animado por pantalla; no se dibuja uno dentro de cada item de una Lazy list.
- Máximo un blur/backdrop activo por banda visual; cards repetidas usan translucidez y borde, no blur individual.
- Canvas y brushes se recuerdan y reutilizan; no se crean objetos de dibujo en cada frame.
- La animación ambiental usa baja frecuencia y se detiene al no estar visible, con reduce motion activo o bajo una ruta de fallback de GPU.
- Dispositivos, widgets, Android Auto y notificaciones usan versiones estáticas de las identidades; no dependen de RenderEffect ni de artwork animado.
- Cada implementación debe perfilar recomposición, memoria y jank en dispositivo físico antes de extender efectos a una superficie nueva.

## Current inventory

- App shell y rutas: `app/VeyraApp.kt`, `app/NowPlayingOverlay.kt`, `ui/navigation/Routes.kt`.
- Pantallas principales: `HomeScreen`, `SearchScreen`, `LibraryScreen`, `DownloadsScreen`, `DetailScreen`, `NowPlayingScreen`, `DevicesScreen`, `SettingsScreen` y pantallas de login/configuración bajo `ui/screens/`.
- Reproductor: `PlayerArtwork`, `PlayerComponents`, `PlayerPanels`, `PlayerQueue`, `PlayerScrubber`, `PlayerTransport` y `PlayerVolume`.
- Componentes: `ui/components/`, `ui/glass/`, `ui/modifiers/`, `ui/foldable/` y `ui/theme/`.
- Recursos de plataforma: iconos launcher/notificación/widget, layouts de widget, fuentes y colores bajo `mobile/android/app/src/main/res/`.

## Implementation handoff

1. El Agente de primitivas implementa el contrato de tokens y componentes sin rediseñar pantallas enteras.
2. Los agentes de shell, reproductor, exploración y ajustes consumen esas primitivas y preservan la matriz de superficies.
3. El Agente de plataforma adapta launcher, notificación y widgets después de que el modelo de tema esté estable.
4. Toda superficie terminada se revisa en Aurora y Cyberpunk, en teléfono compacto y ancho expandido, con movimiento reducido, fuente ampliada y estados de carga/error/vacío.
