# Kit de primitivas Compose — Veyra Music Mobile (Fase 2)

**Sello:** AKASHI DEV
**Estado:** implementado y verificable. No modifica pantallas, navegación, widgets, servicios ni audio.

---

## 1. Archivos compartidos que deben consumir los demás agentes

| Archivo | Contrato |
| --- | --- |
| `ui/theme/VeyraTokens.kt` | Espaciado, radios, tamaños táctiles, opacidades, roles semánticos y tokens Cyber. |
| `ui/theme/VeyraMotion.kt` | `LocalReducedMotion`, `rememberReduceMotion()` (lectura composable), `rememberReducedMotion()` (observador del sistema) y helpers puros de duración. |
| `ui/theme/VeyraTheme.kt` | Provee `LocalAccent` y `LocalReducedMotion` (firma compatible: `reducedMotion` es opcional). |
| `ui/glass/AuroraBackground.kt` | Fondo Aurora con orbes derivados del acento y fallback estático. |
| `ui/glass/GlassSurface.kt` | Superficie glass/sólida con press, focus, disabled y selected. |
| `ui/components/VeyraButtons.kt` | `VeyraButton`, `VeyraIconButton`, `VeyraChip` y enums de variante/tamaño. |
| `ui/components/VeyraSurfaces.kt` | `VeyraCard`, `VeyraDivider`, `VeyraEmptyState`. |
| `ui/components/VeyraCyberPanel.kt` | `CyberPanel` (Neo-Brutalism + Cyberpunk). |
| `ui/components/VeyraModals.kt` | `VeyraModalSheet`, `VeyraSheetHandle`, `VeyraDialogShell`. |
| `ui/components/StatusPillBadge.kt` | Existente; ahora respeta reduce motion y agrupa semántica. |

No redefinir escalas, colores ni curvas en pantallas. Si falta un rol, se añade al token y se reporta al responsable de integración.

---

## 2. Tokens

```kotlin
VeyraSpace.xxs / xs / sm / md / lg / xl / xxl / screenGutter
VeyraRadius.xs / sm / md / lg / xl / dock / pill / button  (+ *Shape)
VeyraSize.touchTarget = 48.dp, iconButton, buttonMedium, buttonSmall, chip,
             iconSm, icon, iconLg, hairline, focusRing
VeyraAlpha.disabledContent / disabledSurface / selectedWash / mutedIcon / scrim
VeyraSemantic.canvas / surface / surfaceRaised / border / textPrimary /
              textSecondary / action / focus / success / warning / error / scrim
VeyraCyber.ink / canvas / surface / surfaceAlt / cyan / magenta / lime / amber /
             borderWidth / hardShadow / shape
VeyraMotion.fastMillis / standardMillis / expressiveMillis / auroraDriftMillis /
             pulseMillis / durationMillis() / shouldAnimate()
```

Reglas:

- El acento vivo se lee de `LocalAccent`, nunca de `CanopyColors.Accent`.
- `VeyraSemantic.action` es el valor por defecto del tema; las primitivas usan `LocalAccent`.
- Ningún texto usa color de acento puro sobre canvas para cuerpos pequeños: el contraste no alcanza 4.5:1.

---

## 3. API y ejemplos

### 3.1 Fondo Aurora

```kotlin
AuroraBackground(
    modifier = Modifier.fillMaxSize(),
    accent = LocalAccent.current,
    active = player.isPlaying,
) {
    // contenido de pantalla
}
```

- Sin RenderEffect: funciona desde API 24.
- Estático con reduce motion, Low RAM o `active = false`.
- Brushes y velos se memorizan; el frame solo hace traslación/escala.

### 3.2 Superficie glass

```kotlin
GlassSurface(
    tone = GlassTone.PANEL,
    shape = VeyraRadius.mdShape,
    selected = item.selected,
    contentPadding = PaddingValues(VeyraSpace.lg),
    onClick = { onOpen(item) },
) {
    // contenido
}
```

- Con cristal activo delega en `glassPane`; apagado cae a Slate sólido + hairline.
- `onClick != null` activa compress elástica, háptica y foco visible.
- `enabled = false` reduce opacidad y retira la acción semántica.

### 3.3 Botones

```kotlin
VeyraButton(
    label = stringResource(R.string.action_play),
    onClick = onPlay,
    leadingIcon = Icons.Rounded.PlayArrow,
)

VeyraButton(
    label = text,
    onClick = onSave,
    variant = VeyraButtonVariant.SECONDARY,
    loading = saving,
    loadingDescription = stringResource(R.string.home_loading),
)

VeyraIconButton(
    icon = Icons.Rounded.FavoriteBorder,
    contentDescription = favoriteLabel,
    selected = isFavorite,
    toggleable = true,
    onClick = onToggleFavorite,
)
```

Variantes: `PRIMARY`, `SECONDARY`, `DANGER`, `GHOST`. Tamaños: `MEDIUM` (48dp) y `SMALL` (36dp visual, 48dp táctil).

### 3.4 Chips, cards y divisores

```kotlin
VeyraChip(label = "Álbumes", selected = filter == ALBUMS, onClick = { ... })

VeyraCard(onClick = { open(item) }) { /* contenido */ }

VeyraDivider(strong = true, inset = VeyraSpace.lg)
```

### 3.5 Estado vacío

```kotlin
VeyraEmptyState(
    icon = Icons.Rounded.CloudOff,
    title = stringResource(R.string.offline_title),
    description = stringResource(R.string.offline_body),
    actionLabel = stringResource(R.string.action_retry),
    onAction = onRetry,
)
```

### 3.6 Panel Cyberpunk

```kotlin
CyberPanel(accent = VeyraCyber.cyan, selected = isLive, onClick = onOpen) {
    Text("LIVE SESSION", style = MaterialTheme.typography.titleMedium)
}
```

- Borde duro de 2dp, sombra sólida de 5dp, esquina 6dp.
- Un solo acento neón por panel; el foco usa `VeyraSemantic.focus`.
- **Alcance pendiente de ratificar:** la entrega de Agente 0 (`PRODUCT.md`/`DESIGN.md` y matriz de superficies) no está en el repositorio. Mientras no exista, Cyberpunk solo debe usarse en estados de alta energía explícitamente aprobados por el responsable de integración; nunca en settings, formularios de cuenta, autenticación ni controles críticos.

### 3.7 Hojas y diálogos

```kotlin
VeyraModalSheet(onDismissRequest = onDismiss) {
    // contenido ColumnScope
}

VeyraDialogShell(onDismissRequest = onDismiss) {
    // contenido ColumnScope
}
```

Usan superficie Slate sólida: la ventana de diálogo no puede muestrear con fiabilidad el backdrop de la ventana principal. Esto evita un glass incorrecto o costoso.

---

## 4. Estados y accesibilidad

| Estado | Implementación |
| --- | --- |
| Pressed | Compresión elástica (0.92–0.985), háptica `TextHandleMove`. |
| Focused | Anillo de 2dp con `VeyraSemantic.focus`. |
| Disabled | Sin `clickable`, opacidad reducida y semántica disabled de Compose. |
| Loading | Spinner, acción bloqueada y `stateDescription` localizado opcional. |
| Selected | Baño de acento + hairline; `toggleable` anuncia checked. |

- Objetivo táctil mínimo: 48dp (`minimumInteractiveComponentSize`).
- Todo icono interactivo exige `contentDescription`.
- `StatusPillBadge` agrupa sus descendientes para TalkBack.
- Texto principal `#F8FAFC` sobre superficies Slate; el acento no se usa como color de cuerpo pequeño.
- Reduce motion se lee de `Settings.Global.ANIMATOR_DURATION_SCALE` una vez por tema.

---

## 5. Rendimiento

- `AuroraBackground`: brushes memorizados, sin allocations por frame; gradientes escalados por transformación.
- Sin `RenderEffect` obligatorio en las primitivas nuevas.
- `remember`/`rememberInfiniteTransition` acotados a la primitiva; nada costoso dentro de `Canvas`/`drawBehind`.
- Press/focus se animan en `graphicsLayer` (sin relayout).
- Low RAM desactiva el movimiento aurora automáticamente.

---

## 6. Verificación

Previews (solo debug):

```text
mobile/android/app/src/debug/java/dev/sfg/veyra/mobile/ui/preview/VeyraPrimitivesPreview.kt
```

Pruebas unitarias de contratos:

```text
mobile/android/app/src/test/java/dev/sfg/veyra/mobile/ui/theme/VeyraTokensTest.kt
```

Comandos:

```bash
cd mobile/android
./gradlew testDebugUnitTest -Dorg.gradle.workers.max=2 --offline
./gradlew assembleDebug -Dorg.gradle.workers.max=2 --offline
cd ../..
npm test
```

---

## 7. Riesgos y pendientes

1. **Falta la entrega de Agente 0.** No existen `PRODUCT.md`, `DESIGN.md` ni matriz de superficies. Los tokens se derivaron de `AGENTS.md` y `mobile/AGENTS.md`; la matriz Cyberpunk debe ratificarse antes de la Fase 6.
2. **Tema claro:** el cliente Android solo declara esquema oscuro. No se inventó un tema claro; si se requiere, es una decisión de Fase 1.
3. **Componentes legacy:** `CanopyButton`, `CanopyPanel` y controles ad-hoc siguen existiendo para no romper pantallas. La migración a las primitivas nuevas corresponde a Fases 3–6.
4. **Glass en hojas:** deliberadamente sólido por aislamiento de ventana; no es una regresión.
5. **Coordinación con el shell (Fase 3):** `LocalCyberpunkTheme` lo declara el agente de shell en `ui/components/PlayerChrome.kt` y lo consumen el rail y el scrubber. El contrato canónico de reduce motion es `dev.sfg.veyra.mobile.ui.theme.rememberReduceMotion`; cualquier import desde `ui.foldable` debe corregirse en la integración.
