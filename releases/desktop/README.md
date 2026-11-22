# 🖥️ Veyra Music — Releases Escritorio (Desktop)

**Sello:** AKASHI DEV  
**Arquitectura:** Electron + Vue 3 + C++/Rust Native Audio DSP  
**Objetivo:** Linux, Windows y macOS  

---

## 📦 Formatos de Distribución (v1.0.3 Oficial)

| Sistema Operativo | Formato | Arquitectura | Tamaño | Estado |
| :--- | :--- | :--- | :--- | :--- |
| **Windows** | Instalador Setup (`Veyra-Setup-1.0.3.exe` / `veyramusic.exe`) | `x64` | 248 MB | ✅ Compilado y verificado |
| **Windows** | Portable Zip (`veyra-v1.0.3-windows-x64.zip`) | `x64` | 404 MB | ✅ Compilado y verificado |
| **Linux** | Standalone Tar.gz (`veyra-v1.0.3-linux-x64.tar.gz`) | `x86_64` | 377 MB | ✅ Compilado y verificado |

---

## 🔐 Sumas de Verificación (SHA-256)

```text
31a5049f0aefdb1ceff0f1b3dbce29beee54f8c77f3c1096943ee2479ac183d0  Veyra-Setup-1.0.3.exe
31a5049f0aefdb1ceff0f1b3dbce29beee54f8c77f3c1096943ee2479ac183d0  veyramusic.exe
b15e24831887be2d9ab062085224c0a5023c6d2ef114b0907ee1ffaf0593c69f  veyra-v1.0.3-windows-x64.zip
61adfc6b23f444352d84229edd1146904e372dc6c40404772b46f2bd24993644  veyra-v1.0.3-linux-x64.tar.gz
```

---

## ⚡ Especificaciones Técnicas

- **Fidelidad Acústica:** Motor Web Audio enriquecido con addon C++/Rust para Smart Crossfade (planificación WSOLA y detección de transitorios).
- **Aceleración Gráfica:** Renderizado por GPU para visualizador de espectro a 60 FPS y fondo dinámico ambiental con `backdrop-filter: blur(28px)`.
- **Integraciones:** Mpris (Linux), SMTC (Windows), Now Playing (macOS), Discord Rich Presence y Last.fm Scrobbler.
- **Sincronización:** Supabase Cloud + base de datos local SQLite (`node:sqlite`).

---

## 🛠️ Ejecución y Empaquetado

### En Linux:
```bash
tar -xzf veyra-v1.0.0-linux-x64.tar.gz
cd veyra-linux-x64
./veyra.sh
```

### En Windows:
1. Descomprimir `veyra-v1.0.0-windows-x64.zip`.
2. Ejecutar `veyra.exe` o `veyra.cmd`.

### Para Recompilar los Paquetes:
```bash
npm run release:desktop
```
