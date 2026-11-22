/* ==========================================================================
 * Veyra Music — Portal Oficial de Descargas (AKASHI DEV)
 * Lógica interactiva de temas (Aurora / Cyberpunk), espectros PCM y descargas.
 * ========================================================================== */

(function () {
  'use strict';

  const OFFICIAL_RELEASES_API = 'https://api.github.com/repos/SCARLXRD-1/VEYRA-MUSIC/releases?per_page=15';
  const RELEASES_PAGE_URL = 'https://github.com/SCARLXRD-1/VEYRA-MUSIC/releases';
  const CACHE_KEY = 'veyra_releases_cache_v3';
  const CACHE_TTL_MS = 15 * 60 * 1000;
  const IS_LOCAL_PREVIEW = ['localhost', '127.0.0.1', ''].includes(window.location.hostname);

  const FALLBACK_VERSION = 'v1.0.4';
  const TV_EXPERIMENTAL_VERSION = 'v1.0.3';

  const PLATFORM_STATES = {
    mobile: 'new',
    tv: 'experimental',
    linux: 'disabled',
    windows: 'disabled'
  };

  let DOWNLOAD_URLS = {
    mobile: IS_LOCAL_PREVIEW ? 'downloads/Veyra-Music-Android-1.0.4-mobile.apk' : 'https://github.com/SCARLXRD-1/VEYRA-MUSIC/releases/download/v1.0.4-mobile/veyramusic.apk',
    tv: `https://github.com/SCARLXRD-1/VEYRA-MUSIC/releases/download/${TV_EXPERIMENTAL_VERSION}/veyramusic.apk`,
    linux: null,
    windows: null
  };

  const OFFICIAL_HASHES = {
    '65111cef4dd6fafaea05d36ef9f0980be66a7f9a649fc87fc4646dd53338793a': {
      file: 'Veyra-Music-Android-1.0.4-mobile.apk',
      platform: 'Android Móvil (Nueva Versión v1.0.4)',
      size: '~41.2 MB'
    },
    '034b197254d114191e95e1fe55f1d452ee216629ee6bd49556157cf67085f999': {
      file: 'Veyra-Music-TV-1.0.3-tv.apk',
      platform: 'Smart TV (Etapa Experimental)',
      size: '~28.9 MB'
    }
  };

  /* -------------------------------------------------------------------------
   * Estado del tema visual y pausa global de animaciones (WCAG 2.2.2)
   * ------------------------------------------------------------------------- */
  let currentVeyraTheme = 'aurora';
  let isVisualizerPaused = false;
  let lastTriggerButton = null;

  function applyVeyraTheme(themeName) {
    const nextTheme = themeName === 'cyberpunk' ? 'cyberpunk' : 'aurora';
    currentVeyraTheme = nextTheme;
    document.documentElement.setAttribute('data-veyra-theme', nextTheme);

    document.querySelectorAll('[data-theme-choice]').forEach(btn => {
      const active = btn.dataset.themeChoice === nextTheme;
      btn.classList.toggle('active', active);
    });

    const stageChip = document.getElementById('stage-mode-chip');
    const rackBadge = document.getElementById('rack-mode-badge');
    if (stageChip) {
      stageChip.textContent = nextTheme === 'cyberpunk' ? '[CYBERPUNK // DSP]' : 'LIQUID AURORA';
    }
    if (rackBadge) {
      rackBadge.textContent = nextTheme === 'cyberpunk' ? '[DSP // CYBERPUNK]' : 'DSP // LIQUID AURORA';
    }
  }

  function setupThemeAndAuraControls() {
    document.querySelectorAll('[data-theme-choice]').forEach(btn => {
      btn.addEventListener('click', () => {
        applyVeyraTheme(btn.dataset.themeChoice);
      });
    });

    const auraBtn = document.getElementById('aura-sync-toggle');
    if (auraBtn) {
      auraBtn.addEventListener('click', () => {
        const enabled = !document.documentElement.classList.contains('has-aura-sync');
        document.documentElement.classList.toggle('has-aura-sync', enabled);
        auraBtn.classList.toggle('active', enabled);
        auraBtn.setAttribute('aria-pressed', String(enabled));
        showToast(enabled ? 'Aura Sync RGB activado' : 'Aura Sync RGB en pausa');
      });
    }

    const visToggle = document.getElementById('visualizer-toggle-btn');
    const stagePlayBtn = document.getElementById('stage-play-btn');

    function updateVisualizerPlayState(paused) {
      isVisualizerPaused = paused;
      document.documentElement.classList.toggle('is-visualizer-paused', paused);

      if (visToggle) {
        visToggle.textContent = paused ? 'Reanudar espectro' : 'Pausar espectro';
      }

      const playIcon = document.getElementById('stage-play-icon');
      if (playIcon) {
        playIcon.innerHTML = paused
          ? '<polygon points="7 4 19 12 7 20 7 4"/>'
          : '<rect x="6" y="4" width="4" height="16" rx="1"/><rect x="14" y="4" width="4" height="16" rx="1"/>';
      }
    }

    if (visToggle) {
      visToggle.addEventListener('click', () => updateVisualizerPlayState(!isVisualizerPaused));
    }
    if (stagePlayBtn) {
      stagePlayBtn.addEventListener('click', () => updateVisualizerPlayState(!isVisualizerPaused));
    }

    applyVeyraTheme('aurora');
  }

  /* -------------------------------------------------------------------------
   * 1. Detección inteligente de plataforma
   * ------------------------------------------------------------------------- */
  function detectClientPlatform() {
    const ua = (navigator.userAgent || '').toLowerCase();
    const isTV = /smart-tv|smarttv|googletv|appletv|hbbtv|pov_tv|netcast|webos|tizen|android tv|aftb|aftt|aftm/.test(ua);
    if (isTV) return 'tv';

    if (/android/.test(ua)) return 'mobile';

    const desktopRedirectMeta = 'Linux y Windows pausados temporalmente • Usa Android Móvil v1.0.4';
    return {
      isDesktopPaused: /win|mac|linux/.test(ua),
      meta: desktopRedirectMeta
    };
  }

  function setupAutoDetection() {
    const detectedInfo = detectClientPlatform();
    const platform = typeof detectedInfo === 'string' ? detectedInfo : 'mobile';
    const isDesktopPaused = typeof detectedInfo === 'object' && detectedInfo.isDesktopPaused;

    const iconEl = document.getElementById('detected-icon');
    const nameEl = document.getElementById('detected-platform-name');
    const archEl = document.getElementById('detected-arch-badge');
    const btnEl = document.getElementById('detected-download-btn');
    const btnLabelEl = document.getElementById('detected-btn-label');
    const metaEl = document.getElementById('detected-file-meta');

    const mobileSvg = '<svg viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><rect x="6" y="2" width="12" height="20" rx="3"/><line x1="12" y1="18" x2="12.01" y2="18"/></svg>';
    const tvSvg = '<svg viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="4" width="20" height="14" rx="2"/><line x1="8" y1="21" x2="16" y2="21"/><line x1="12" y1="18" x2="12" y2="21"/></svg>';

    if (platform === 'tv') {
      if (iconEl) iconEl.innerHTML = tvSvg;
      if (nameEl) nameEl.textContent = 'Smart TV (Etapa Experimental)';
      if (archEl) archEl.textContent = `APK · ${TV_EXPERIMENTAL_VERSION} Beta`;
      if (btnEl) {
        btnEl.href = DOWNLOAD_URLS.tv;
        btnEl.setAttribute('download', `Veyra-Music-TV-${TV_EXPERIMENTAL_VERSION.replace(/^v/, '')}-tv.apk`);
      }
      if (btnLabelEl) btnLabelEl.textContent = `Descargar TV Experimental ${TV_EXPERIMENTAL_VERSION}`;
      if (metaEl) {
        metaEl.innerHTML = '<span>Android TV / Google TV</span><span class="meta-separator">&bull;</span><span>Etapa Experimental</span>';
      }
      return;
    }

    if (iconEl) iconEl.innerHTML = mobileSvg;
    if (nameEl) nameEl.textContent = 'Android Móvil — Nueva Versión';
    if (archEl) archEl.textContent = `APK · ${FALLBACK_VERSION}`;
    if (btnEl) {
      btnEl.href = DOWNLOAD_URLS.mobile;
      btnEl.setAttribute('download', 'Veyra-Music-Android-1.0.4-mobile.apk');
    }
    if (btnLabelEl) btnLabelEl.textContent = `Descargar Nueva Versión Móvil (${FALLBACK_VERSION})`;
    if (metaEl) {
      metaEl.innerHTML = isDesktopPaused
        ? `<span>${detectedInfo.meta}</span>`
        : '<span>Android 10+ Universal</span><span class="meta-separator">&bull;</span><span>Liquid Aurora + Cyberpunk</span>';
    }
  }

  /* -------------------------------------------------------------------------
   * 2. Filtrado de Plataformas mediante Tabs
   * ------------------------------------------------------------------------- */
  function setupPlatformTabs() {
    const tabs = document.querySelectorAll('.tab-btn');
    const cards = document.querySelectorAll('.release-card');

    tabs.forEach(tab => {
      tab.addEventListener('click', () => {
        tabs.forEach(t => {
          t.classList.remove('active');
          t.setAttribute('aria-selected', 'false');
        });
        tab.classList.add('active');
        tab.setAttribute('aria-selected', 'true');

        const filter = tab.dataset.filter;
        cards.forEach(card => {
          card.style.display = (filter === 'all' || card.dataset.category === filter) ? 'flex' : 'none';
        });
      });
    });
  }

  /* -------------------------------------------------------------------------
   * 3. Toast, Copiado y Acordeón ADB
   * ------------------------------------------------------------------------- */
  function showToast(message) {
    const toast = document.getElementById('toast');
    const messageEl = document.getElementById('toast-message');
    if (!toast || !messageEl) return;

    messageEl.textContent = message;
    toast.classList.remove('is-hidden');

    clearTimeout(window.__toastTimer);
    window.__toastTimer = setTimeout(() => {
      toast.classList.add('is-hidden');
    }, 3000);
  }

  function setupCopyButtons() {
    document.addEventListener('click', async (e) => {
      const btn = e.target.closest('.copy-btn');
      if (!btn) return;
      const hash = btn.dataset.checksum;
      if (!hash) return;

      try {
        await navigator.clipboard.writeText(hash);
        showToast('Firma SHA-256 copiada al portapapeles');
      } catch {
        const tempInput = document.createElement('input');
        tempInput.value = hash;
        document.body.appendChild(tempInput);
        tempInput.select();
        document.execCommand('copy');
        document.body.removeChild(tempInput);
        showToast('Firma SHA-256 copiada');
      }
    });
  }

  function setupInstructionToggles() {
    document.querySelectorAll('.toggle-instructions-btn').forEach(btn => {
      btn.addEventListener('click', () => {
        const box = document.getElementById(btn.dataset.target);
        if (!box) return;
        const isHidden = box.classList.toggle('is-hidden');
        const textSpan = btn.querySelector('span');
        if (textSpan) {
          textSpan.textContent = isHidden ? 'Guía de instalación en TV (ADB)' : 'Ocultar guía ADB';
        }
      });
    });
  }

  /* -------------------------------------------------------------------------
   * 4. Verificador SHA-256
   * ------------------------------------------------------------------------- */
  function setupHashVerifier() {
    const input = document.getElementById('sha-input');
    const btn = document.getElementById('verify-btn');
    const resultBox = document.getElementById('verify-result');
    if (!input || !btn || !resultBox) return;

    function verify() {
      const query = (input.value || '').trim().toLowerCase();
      if (!query) {
        resultBox.className = 'verify-result is-hidden';
        resultBox.textContent = '';
        return;
      }

      const match = OFFICIAL_HASHES[query];
      if (match) {
        resultBox.className = 'verify-result is-valid';
        resultBox.innerHTML = `<svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><polyline points="20 6 9 17 4 12"/></svg><div><strong>Firma verificada:</strong> Corresponde a <strong>${match.file}</strong> (${match.platform}, ${match.size}).</div>`;
      } else {
        resultBox.className = 'verify-result is-invalid';
        resultBox.innerHTML = `<svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg><div><strong>Sin coincidencia:</strong> El hash ingresado no coincide con ningún paquete oficial actual.</div>`;
      }
    }

    btn.addEventListener('click', verify);
    input.addEventListener('keydown', (e) => {
      if (e.key === 'Enter') verify();
    });
  }

  /* -------------------------------------------------------------------------
   * 5. Modal QR Móvil
   * ------------------------------------------------------------------------- */
  function generateQrMatrix(text) {
    const size = 25;
    const matrix = Array.from({ length: size }, () => Array(size).fill(0));
    function addFinder(sx, sy) {
      for (let y = 0; y < 7; y++) {
        for (let x = 0; x < 7; x++) {
          if (x === 0 || x === 6 || y === 0 || y === 6 || (x >= 2 && x <= 4 && y >= 2 && y <= 4)) {
            matrix[sy + y][sx + x] = 1;
          }
        }
      }
    }
    addFinder(0, 0);
    addFinder(size - 7, 0);
    addFinder(0, size - 7);

    let seed = 0;
    for (let i = 0; i < text.length; i++) seed = (seed * 31 + text.charCodeAt(i)) >>> 0;
    function nextRand() {
      seed = (seed * 1664525 + 1013904223) >>> 0;
      return (seed >>> 16) / 65536;
    }
    for (let y = 0; y < size; y++) {
      for (let x = 0; x < size; x++) {
        const inFinder = (x < 8 && y < 8) || (x >= size - 8 && y < 8) || (x < 8 && y >= size - 8);
        if (!inFinder) matrix[y][x] = nextRand() > 0.5 ? 1 : 0;
      }
    }
    return matrix;
  }

  function drawQrCanvas(canvasId, text) {
    const canvas = document.getElementById(canvasId);
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    const matrix = generateQrMatrix(text);
    const cell = canvas.width / matrix.length;
    ctx.fillStyle = '#FFFFFF';
    ctx.fillRect(0, 0, canvas.width, canvas.height);
    ctx.fillStyle = '#090718';
    matrix.forEach((row, y) => {
      row.forEach((val, x) => {
        if (val === 1) ctx.fillRect(Math.floor(x * cell), Math.floor(y * cell), Math.ceil(cell), Math.ceil(cell));
      });
    });
  }

  function setupQrModal() {
    const modal = document.getElementById('qr-modal-mobile');
    const closeBtn = document.getElementById('qr-modal-close');
    const urlHint = document.getElementById('qr-url-display');
    if (!modal) return;

    function openModal(trigger) {
      lastTriggerButton = trigger || document.activeElement;
      const qrTarget = IS_LOCAL_PREVIEW
        ? `${window.location.origin}/downloads/Veyra-Music-Android-1.0.4-mobile.apk`
        : DOWNLOAD_URLS.mobile;
      drawQrCanvas('qr-code-canvas', qrTarget);
      if (urlHint) urlHint.textContent = qrTarget;
      modal.classList.remove('is-hidden');
      if (closeBtn) closeBtn.focus();
    }

    function closeModal() {
      modal.classList.add('is-hidden');
      if (lastTriggerButton && typeof lastTriggerButton.focus === 'function') {
        lastTriggerButton.focus();
      }
    }

    document.querySelectorAll('.show-qr-btn').forEach(btn => {
      btn.addEventListener('click', () => openModal(btn));
    });
    if (closeBtn) closeBtn.addEventListener('click', closeModal);
    modal.addEventListener('click', (e) => {
      if (e.target === modal) closeModal();
    });
    document.addEventListener('keydown', (e) => {
      if (e.key === 'Escape' && !modal.classList.contains('is-hidden')) closeModal();
    });
  }

  /* -------------------------------------------------------------------------
   * 6. Visualizadores Unificados Veyra (Radial Aurora / Barras Cyber + Onda + Rack)
   * ------------------------------------------------------------------------- */
  function setupLiveVisualizers() {
    const showcaseCanvas = document.getElementById('showcase-spectrum-canvas');
    const scrubberCanvas = document.getElementById('showcase-wave-scrubber');
    const rackCanvas = document.getElementById('live-wave-canvas');
    if (!showcaseCanvas && !scrubberCanvas && !rackCanvas) return;

    const reducedMotion = window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    let phase = 0.6;
    let isTabVisible = true;
    let rafId = null;

    document.addEventListener('visibilitychange', () => {
      isTabVisible = !document.hidden;
    });

    function computeBandEnergy(index, total, t) {
      const n = index / total;
      const bass = Math.exp(-n * 4.2) * (0.55 + 0.45 * Math.sin(t * 1.9));
      const mid = 0.42 * (0.5 + 0.5 * Math.sin(t * 2.3 + index * 0.45));
      const high = 0.25 * (0.5 + 0.5 * Math.cos(t * 3.1 - index * 0.72));
      return Math.min(1, Math.max(0.12, bass * 0.6 + mid * 0.45 + high * 0.35));
    }

    function drawShowcaseSpectrum(t) {
      if (!showcaseCanvas) return;
      const ctx = showcaseCanvas.getContext('2d');
      const w = showcaseCanvas.width;
      const h = showcaseCanvas.height;
      const cx = w / 2;
      const cy = h / 2;
      ctx.clearRect(0, 0, w, h);

      if (currentVeyraTheme === 'cyberpunk') {
        // Barras acid yellow alrededor inferior estilo CyberpunkArtworkSpectrum
        const bars = 26;
        const margin = 34;
        const usableW = w - margin * 2;
        const step = usableW / bars;
        for (let i = 0; i < bars; i++) {
          const energy = computeBandEnergy(i, bars, t);
          const barH = 10 + energy * 42;
          const x = margin + i * step;
          const y = h - 30 - barH;
          ctx.fillStyle = i % 6 === 0 ? '#22D3EE' : '#FACC15';
          ctx.fillRect(x, y, Math.max(4, step - 3), barH);
        }
        return;
      }

      // Espectro radial 48 rayos alrededor del disco Liquid Aurora
      const rays = 48;
      const baseR = 82;
      for (let i = 0; i < rays; i++) {
        const angle = (i / rays) * Math.PI * 2 - Math.PI / 2;
        const energy = computeBandEnergy(i % 16, 16, t);
        const len = 8 + energy * 34;
        const x1 = cx + Math.cos(angle) * baseR;
        const y1 = cy + Math.sin(angle) * baseR;
        const x2 = cx + Math.cos(angle) * (baseR + len);
        const y2 = cy + Math.sin(angle) * (baseR + len);

        ctx.strokeStyle = i % 3 === 0 ? 'rgba(56, 189, 248, 0.85)' : i % 3 === 1 ? 'rgba(139, 92, 246, 0.82)' : 'rgba(236, 72, 153, 0.72)';
        ctx.lineWidth = 3;
        ctx.lineCap = 'round';
        ctx.beginPath();
        ctx.moveTo(x1, y1);
        ctx.lineTo(x2, y2);
        ctx.stroke();
      }
    }

    function drawScrubberWave(t) {
      if (!scrubberCanvas) return;
      const ctx = scrubberCanvas.getContext('2d');
      const w = scrubberCanvas.width;
      const h = scrubberCanvas.height;
      ctx.clearRect(0, 0, w, h);

      const bars = 44;
      const step = w / bars;
      const activeBars = 17;
      for (let i = 0; i < bars; i++) {
        const energy = computeBandEnergy(i, bars, t * 0.9);
        const barH = 6 + energy * (h - 10);
        const x = i * step + 1.5;
        const y = (h - barH) / 2;
        const isPlayed = i < activeBars;
        if (currentVeyraTheme === 'cyberpunk') {
          ctx.fillStyle = isPlayed ? '#FACC15' : 'rgba(34, 211, 238, 0.28)';
        } else {
          ctx.fillStyle = isPlayed ? '#F8FAFC' : 'rgba(195, 207, 226, 0.28)';
        }
        ctx.beginPath();
        ctx.roundRect(x, y, Math.max(2.5, step - 3.5), barH, 3);
        ctx.fill();
      }
    }

    function drawRackSpectrum(t) {
      if (!rackCanvas) return;
      const ctx = rackCanvas.getContext('2d');
      const w = rackCanvas.width;
      const h = rackCanvas.height;
      ctx.clearRect(0, 0, w, h);

      const isCyber = currentVeyraTheme === 'cyberpunk';
      const bars = 56;
      const barStep = w / bars;

      for (let i = 0; i < bars; i++) {
        const e = computeBandEnergy(i, bars, t);
        const bh = 8 + e * (h * 0.56);
        const x = i * barStep + 2;
        const y = h - bh - 4;
        ctx.fillStyle = isCyber
          ? (i % 7 === 0 ? 'rgba(250, 204, 21, 0.55)' : 'rgba(34, 211, 238, 0.28)')
          : 'rgba(139, 92, 246, 0.22)';
        ctx.fillRect(x, y, Math.max(3, barStep - 5), bh);
      }

      ctx.beginPath();
      ctx.lineWidth = 2.4;
      ctx.strokeStyle = isCyber ? '#FACC15' : '#38BDF8';
      for (let x = 0; x <= w; x += 6) {
        const norm = x / w;
        const env = Math.sin(norm * Math.PI);
        const y = h * 0.44 + Math.sin(norm * 14 + t * 2) * 18 * env + Math.cos(norm * 28 - t * 1.4) * 7 * env;
        if (x === 0) ctx.moveTo(x, y);
        else ctx.lineTo(x, y);
      }
      ctx.stroke();
    }

    function renderFrame() {
      drawShowcaseSpectrum(phase);
      drawScrubberWave(phase);
      drawRackSpectrum(phase);

      if (!reducedMotion && !isVisualizerPaused && isTabVisible) {
        phase += 0.032;
      }
      rafId = window.requestAnimationFrame(renderFrame);
    }

    if (reducedMotion) {
      drawShowcaseSpectrum(phase);
      drawScrubberWave(phase);
      drawRackSpectrum(phase);
      return;
    }

    rafId = window.requestAnimationFrame(renderFrame);
    window.addEventListener('beforeunload', () => {
      if (rafId) window.cancelAnimationFrame(rafId);
    });
  }

  /* -------------------------------------------------------------------------
   * 7. Control de descargas y botones deshabilitados (aria-disabled)
   * ------------------------------------------------------------------------- */
  function updateDownloadLinks(mobileAsset) {
    const mobileBtn = document.getElementById('mobile-download-btn');
    const mobileLabel = document.getElementById('mobile-btn-label');
    const mobileVersion = document.getElementById('mobile-version-text');
    const headerVer = document.getElementById('header-version-text');
    const heroPill = document.getElementById('hero-pill-version');

    const latestVersion = mobileAsset ? mobileAsset.version : FALLBACK_VERSION;
    if (mobileAsset && mobileAsset.url && !IS_LOCAL_PREVIEW) {
      DOWNLOAD_URLS.mobile = mobileAsset.url;
    }

    if (mobileBtn) mobileBtn.href = DOWNLOAD_URLS.mobile;
    if (mobileLabel) mobileLabel.textContent = `Descargar Nueva Versión Móvil (${latestVersion})`;
    if (mobileVersion) mobileVersion.textContent = `${latestVersion} • Android 10+ (ARM64 / Universal)`;
    if (headerVer) headerVer.textContent = `${latestVersion} Móvil`;
    if (heroPill) {
      heroPill.innerHTML = `<span class="pulse-dot" aria-hidden="true"></span><span>Nueva Versión Móvil ${latestVersion} • Liquid Aurora + Cyberpunk</span>`;
    }

    const tvBtn = document.getElementById('tv-download-btn');
    const tvLabel = document.getElementById('tv-btn-label');
    const tvVersion = document.getElementById('tv-version-text');
    if (tvBtn) tvBtn.href = DOWNLOAD_URLS.tv;
    if (tvLabel) tvLabel.textContent = `Descargar TV Experimental (${TV_EXPERIMENTAL_VERSION})`;
    if (tvVersion) tvVersion.textContent = `${TV_EXPERIMENTAL_VERSION} • Beta Experimental (10-Foot UI)`;

    ['linux', 'windows'].forEach(id => {
      const btn = document.getElementById(`${id}-download-btn`);
      if (btn) {
        btn.setAttribute('aria-disabled', 'true');
        if (PLATFORM_STATES[id] === 'disabled') {
          btn.dataset.state = 'disabled';
        }
      }
    });
  }

  function setupDownloadFeedback() {
    document.addEventListener('click', (e) => {
      const disabledBtn = e.target.closest('[aria-disabled="true"]');
      if (disabledBtn) {
        e.preventDefault();
        const plat = disabledBtn.dataset.disabledPlatform || 'Esta plataforma';
        showToast(`${plat} está deshabilitado temporalmente. Disponible hoy: Android Móvil ${FALLBACK_VERSION}.`);
        return;
      }

      const dlBtn = e.target.closest('a[download], #tv-download-btn');
      if (!dlBtn) return;

      const label = dlBtn.id === 'tv-download-btn'
        ? 'Descargando Smart TV Experimental (.apk)...'
        : `Preparando ${dlBtn.getAttribute('download') || 'Veyra Móvil v1.0.4'}...`;
      showToast(label);
    });
  }

  /* -------------------------------------------------------------------------
   * 8. Historial de Releases (con caché + fallback local)
   * ------------------------------------------------------------------------- */
  function renderLocalVersionHistory(container) {
    container.innerHTML = `
      <div class="history-list">
        <div class="history-entry">
          <div class="history-entry-header">
            <span class="history-tag">${FALLBACK_VERSION} • Android Móvil (Nueva Versión)</span>
            <span class="history-date">Estado: Disponible</span>
          </div>
          <div class="history-notes">• Estreno de doble interfaz: Liquid Aurora y Cyberpunk UI.
• Espectro PCM de 12 bandas en tiempo real + Aura Sync RGB rítmico.
• Motor EQ de 10 bandas, Crossfade WSOLA, Crossfeed y limitador.</div>
        </div>
        <div class="history-entry">
          <div class="history-entry-header">
            <span class="history-tag">${TV_EXPERIMENTAL_VERSION} • Smart TV (Etapa Experimental)</span>
            <span class="history-date">Estado: Experimental</span>
          </div>
          <div class="history-notes">• Compilación beta 10-Foot UI optimizada para Android TV / Google TV y control D-Pad.</div>
        </div>
        <div class="history-entry">
          <div class="history-entry-header">
            <span class="history-tag">Linux x64 &amp; Windows x64</span>
            <span class="history-date">Estado: Deshabilitado temporalmente</span>
          </div>
          <div class="history-notes">• Distribución de escritorio pausada mientras enfocamos este lanzamiento en móvil y Smart TV experimental.</div>
        </div>
      </div>
    `;
  }

  async function fetchReleasesWithCache() {
    try {
      const cached = localStorage.getItem(CACHE_KEY);
      if (cached) {
        const parsed = JSON.parse(cached);
        if (Date.now() - parsed.timestamp < CACHE_TTL_MS && Array.isArray(parsed.data)) {
          return parsed.data;
        }
      }
      const res = await fetch(OFFICIAL_RELEASES_API, { headers: { Accept: 'application/vnd.github.v3+json' } });
      if (!res.ok) throw new Error('API Error');
      const data = await res.json();
      localStorage.setItem(CACHE_KEY, JSON.stringify({ timestamp: Date.now(), data }));
      return data;
    } catch {
      return [];
    }
  }

  function setupVersionHistoryModal() {
    const modal = document.getElementById('version-history-modal');
    const openBtn = document.getElementById('version-history-btn');
    const closeBtn = document.getElementById('history-modal-close');
    const container = document.getElementById('history-list-container');
    if (!modal || !openBtn || !container) return;

    function closeModal() {
      modal.classList.add('is-hidden');
      openBtn.focus();
    }

    openBtn.addEventListener('click', async () => {
      modal.classList.remove('is-hidden');
      if (closeBtn) closeBtn.focus();
      renderLocalVersionHistory(container);

      const releases = await fetchReleasesWithCache();
      if (Array.isArray(releases) && releases.length > 0 && !IS_LOCAL_PREVIEW) {
        const first = releases[0];
        if (first && first.tag_name) {
          const item = document.createElement('div');
          item.className = 'history-entry';
          item.innerHTML = `<div class="history-entry-header"><span class="history-tag">Último tag remoto: ${first.tag_name}</span><a href="${RELEASES_PAGE_URL}" target="_blank" rel="noopener noreferrer" class="history-date">Ver en GitHub</a></div>`;
          container.querySelector('.history-list')?.appendChild(item);
        }
      }
    });

    if (closeBtn) closeBtn.addEventListener('click', closeModal);
    modal.addEventListener('click', (e) => {
      if (e.target === modal) closeModal();
    });
    document.addEventListener('keydown', (e) => {
      if (e.key === 'Escape' && !modal.classList.contains('is-hidden')) closeModal();
    });
  }

  /* -------------------------------------------------------------------------
   * Inicialización
   * ------------------------------------------------------------------------- */
  document.addEventListener('DOMContentLoaded', () => {
    setupThemeAndAuraControls();
    setupAutoDetection();
    setupPlatformTabs();
    setupCopyButtons();
    setupInstructionToggles();
    setupHashVerifier();
    setupQrModal();
    setupLiveVisualizers();
    updateDownloadLinks(null);
    setupDownloadFeedback();
    setupVersionHistoryModal();
  });
})();
