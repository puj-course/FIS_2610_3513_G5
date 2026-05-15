/**
 * exportHorario.js — HU: Exportación de Horario en PDF/Imagen
 * Sub-issue 1: Integración de librerías y estructura base del módulo
 *
 * Decisión de arquitectura: implementación 100% frontend-only.
 *
 * Justificación:
 *  - El nodo DOM del horario ya está completamente renderizado en el cliente
 *    (grilla CSS + bloques de materia posicionados con position:absolute).
 *  - html2canvas captura el DOM tal como lo ve el usuario, incluyendo colores,
 *    fuentes y overlaps de bloques — sin necesidad de re-renderizar en el servidor.
 *  - jsPDF opera sobre el canvas resultante, por lo que tampoco requiere backend.
 *  - Evita añadir un endpoint REST adicional, reduciendo la carga en el servidor
 *    Render (plan gratuito con tiempo de inactividad).
 *  - Las librerías se cargan via CDN (sin instalación local), consistent con el
 *    patrón ya establecido en index.html para chart.js, jsPDF, etc.
 *
 * Librerías usadas:
 *  - html2canvas  v1.4.1  → https://cdnjs.cloudflare.com/ajax/libs/html2canvas/1.4.1/html2canvas.min.js
 *  - jsPDF        v2.5.1  → https://cdnjs.cloudflare.com/ajax/libs/jspdf/2.5.1/jspdf.umd.min.js
 *  - jspdf-autotable v3.5.25 → ya cargado en index.html (usado por exportarInformePDF)
 *
 * Nodo DOM objetivo del horario: #horario-grid-wrapper > #horario-grid
 */

// ── Constantes del módulo ────────────────────────────────────────────────────

/** Selector del contenedor que se capturará con html2canvas */
const HORARIO_SELECTOR = '#horario-grid-wrapper';

/** Escala de captura: 2 = resolución 2x (evita imágenes borrosas en pantallas HiDPI) */
const CAPTURE_SCALE = 2;

/** Nombre base para los archivos descargados (sin extensión) */
const EXPORT_FILENAME_BASE = 'horario_studyhub';

// ── Verificación de dependencias ─────────────────────────────────────────────

/**
 * Verifica que las librerías necesarias estén disponibles en window.
 * Se llama antes de cualquier exportación para dar feedback temprano.
 *
 * @returns {{ ok: boolean, missing: string[] }}
 */
function verificarDependencias() {
    const missing = [];
    if (typeof window.html2canvas === 'undefined') missing.push('html2canvas');
    if (typeof window.jspdf === 'undefined')       missing.push('jsPDF');
    return { ok: missing.length === 0, missing };
}

// ── Captura del DOM ──────────────────────────────────────────────────────────

/**
 * capturaHorario()
 * Captura el nodo DOM del horario usando html2canvas.
 * Aplica configuración optimizada para grillas con colores y texto pequeño.
 *
 * @returns {Promise<HTMLCanvasElement>} Canvas con la imagen del horario
 * @throws {Error} Si el nodo no existe o html2canvas falla
 */
async function capturaHorario() {
    const nodo = document.querySelector(HORARIO_SELECTOR);
    if (!nodo) {
        throw new Error(
            `[exportHorario] No se encontró el nodo "${HORARIO_SELECTOR}". ` +
            'Asegúrate de estar en la vista del Horario Semanal antes de exportar.'
        );
    }

    // Scroll al inicio para capturar toda la grilla desde arriba
    nodo.scrollTop = 0;

    // html2canvas necesita dimensiones reales. El wrapper tiene overflow-x:auto
    // y puede reportar width=0 si el contenedor está colapsado. Se fuerza
    // temporalmente tamaño y overflow visibles para la captura.
    const estilosOriginales = {
        width:    nodo.style.width,
        minWidth: nodo.style.minWidth,
        overflow: nodo.style.overflow,
    };

    const anchoReal = nodo.scrollWidth || nodo.offsetWidth;
    if (anchoReal === 0) {
        nodo.style.width    = '900px';
        nodo.style.minWidth = '900px';
    }
    nodo.style.overflow = 'visible';

    let canvas;
    try {
        canvas = await window.html2canvas(nodo, {
            scale:           CAPTURE_SCALE,
            useCORS:         true,
            allowTaint:      false,
            backgroundColor: '#FFFFFF',
            logging:         false,
            scrollX:         0,
            scrollY:         -window.scrollY,
            width:           nodo.scrollWidth  || nodo.offsetWidth  || 900,
            height:          nodo.scrollHeight || nodo.offsetHeight || 600,
            windowWidth:     document.documentElement.scrollWidth,
            windowHeight:    document.documentElement.scrollHeight,
        });
    } finally {
        // Restaurar estilos originales pase lo que pase
        nodo.style.width    = estilosOriginales.width;
        nodo.style.minWidth = estilosOriginales.minWidth;
        nodo.style.overflow = estilosOriginales.overflow;
    }

    return canvas;
}

// ── Exportación a PDF ────────────────────────────────────────────────────────

/**
 * exportarHorarioPDF()
 * Exporta el horario visible como archivo PDF descargable.
 * El PDF usa orientación landscape para aprovechar el ancho de la grilla.
 *
 * Flujo:
 *  1. Captura el DOM con html2canvas
 *  2. Crea un PDF jsPDF en landscape A4
 *  3. Inserta el canvas como imagen JPEG centrada
 *  4. Agrega encabezado con título y fecha
 *  5. Dispara la descarga
 *
 * @returns {Promise<void>}
 */
async function exportarHorarioPDF() {
    const { ok, missing } = verificarDependencias();
    if (!ok) {
        console.error('[exportHorario] Librerías faltantes:', missing);
        if (typeof showToast === 'function') {
            showToast(`Error: librerías no cargadas (${missing.join(', ')})`);
        }
        return;
    }

    try {
        _setExportLoading(true, 'pdf');

        const canvas = await capturaHorario();

        const { jsPDF }  = window.jspdf;
        const doc        = new jsPDF({ orientation: 'landscape', unit: 'mm', format: 'a4' });

        // Dimensiones del PDF (A4 landscape: 297mm × 210mm)
        const PAGE_W = doc.internal.pageSize.getWidth();
        const PAGE_H = doc.internal.pageSize.getHeight();
        const MARGIN  = 12; // mm

        // ── Encabezado ──
        const fechaStr = new Date().toLocaleDateString('es-ES', {
            weekday: 'long', day: '2-digit', month: 'long', year: 'numeric'
        });

        doc.setFontSize(16);
        doc.setTextColor(21, 101, 192);   // --blue-600
        doc.text('StudyHub · Horario Semanal', MARGIN, MARGIN + 4);

        doc.setFontSize(9);
        doc.setTextColor(134, 142, 150);  // --gray-500
        doc.text(`Generado el ${fechaStr}`, PAGE_W - MARGIN, MARGIN + 4, { align: 'right' });

        // Línea separadora
        doc.setDrawColor(222, 226, 230);  // --gray-300
        doc.line(MARGIN, MARGIN + 8, PAGE_W - MARGIN, MARGIN + 8);

        // ── Imagen del horario ──
        if (!canvas.width || !canvas.height) {
            throw new Error('El canvas capturado tiene dimensiones inválidas.');
        }

        const imgData  = canvas.toDataURL('image/jpeg', 0.95);
        const topImg   = MARGIN + 12;
        const maxImgW  = PAGE_W - MARGIN * 2;
        const maxImgH  = PAGE_H - topImg - MARGIN;
        const ratio    = canvas.width / canvas.height;

        let finalW = maxImgW;
        let finalH = finalW / ratio;
        if (finalH > maxImgH) {
            finalH = maxImgH;
            finalW = finalH * ratio;
        }
        finalW = Math.max(finalW, 1);
        finalH = Math.max(finalH, 1);

        const leftImg = MARGIN + (maxImgW - finalW) / 2;

        doc.addImage(imgData, 'JPEG', leftImg, topImg, finalW, finalH);

        // ── Pie de página ──
        doc.setFontSize(8);
        doc.setTextColor(173, 181, 189);
        doc.text('StudyHub App', PAGE_W / 2, PAGE_H - 4, { align: 'center' });

        // ── Descarga ──
        const fecha   = new Date().toISOString().split('T')[0];
        doc.save(`${EXPORT_FILENAME_BASE}_${fecha}.pdf`);

        if (typeof showToast === 'function') showToast('Horario exportado como PDF');

    } catch (err) {
        console.error('[exportHorario] Error al exportar PDF:', err);
        if (typeof showToast === 'function') showToast('Error al generar el PDF. Intenta de nuevo.');
    } finally {
        _setExportLoading(false, 'pdf');
    }
}

// ── Exportación a Imagen (JPG) ───────────────────────────────────────────────

/**
 * exportarHorarioImagen()
 * Exporta el horario visible como archivo JPG descargable.
 * Usa un elemento <a> temporal para disparar la descarga sin abrir nuevas pestañas.
 *
 * @returns {Promise<void>}
 */
async function exportarHorarioImagen() {
    const { ok, missing } = verificarDependencias();
    if (!ok) {
        console.error('[exportHorario] Librerías faltantes:', missing);
        if (typeof showToast === 'function') {
            showToast(`Error: librerías no cargadas (${missing.join(', ')})`);
        }
        return;
    }

    try {
        _setExportLoading(true, 'img');

        const canvas  = await capturaHorario();
        const imgData = canvas.toDataURL('image/jpeg', 0.95);

        const fecha   = new Date().toISOString().split('T')[0];
        const link    = document.createElement('a');
        link.href     = imgData;
        link.download = `${EXPORT_FILENAME_BASE}_${fecha}.jpg`;
        link.click();

        if (typeof showToast === 'function') showToast('Horario exportado como imagen JPG');

    } catch (err) {
        console.error('[exportHorario] Error al exportar imagen:', err);
        if (typeof showToast === 'function') showToast('Error al generar la imagen. Intenta de nuevo.');
    } finally {
        _setExportLoading(false, 'img');
    }
}

// ── Estado visual de carga ───────────────────────────────────────────────────

/**
 * _setExportLoading(loading, tipo)
 * Actualiza el estado visual de los botones de exportación mientras
 * se procesa la captura (puede tardar ~500ms en grillas grandes).
 *
 * @param {boolean} loading - true para mostrar spinner, false para restaurar
 * @param {'pdf'|'img'} tipo - qué botón actualizar
 */
function _setExportLoading(loading, tipo) {
    const btnPdf = document.getElementById('export-horario-pdf-btn');
    const btnImg = document.getElementById('export-horario-img-btn');
    const btn    = tipo === 'pdf' ? btnPdf : btnImg;
    if (!btn) return;

    if (loading) {
        btn.dataset.originalHtml = btn.innerHTML;
        btn.innerHTML = `<span class="material-icons-round" style="animation:spin .7s linear infinite">sync</span> Generando...`;
        btn.disabled  = true;
    } else {
        if (btn.dataset.originalHtml) btn.innerHTML = btn.dataset.originalHtml;
        btn.disabled = false;
        delete btn.dataset.originalHtml;
    }
}

// ── Verificación de carga al inicio ─────────────────────────────────────────

/**
 * Verifica al cargar el script que html2canvas esté disponible.
 * Registra en consola el estado de todas las dependencias de exportación.
 * No bloquea la app si falta alguna.
 */
document.addEventListener('DOMContentLoaded', () => {
    const { ok, missing } = verificarDependencias();

    if (ok) {
        console.info(
            '[exportHorario] ✔ Dependencias cargadas correctamente:\n' +
            '  · html2canvas disponible en window.html2canvas\n' +
            '  · jsPDF disponible en window.jspdf'
        );

        // Smoke test: verificar que el nodo objetivo existe (puede no estar visible aún)
        const nodo = document.querySelector(HORARIO_SELECTOR);
        console.info(
            nodo
                ? `[exportHorario] ✔ Nodo objetivo "${HORARIO_SELECTOR}" encontrado en el DOM`
                : `[exportHorario] ⚠ Nodo "${HORARIO_SELECTOR}" aún no visible (normal: se renderiza al abrir la vista)`
        );
    } else {
        console.warn(
            `[exportHorario] ⚠ Librerías faltantes: ${missing.join(', ')}.\n` +
            'Verifica los CDN en el <head> de index.html.'
        );
    }
});

// ── API pública del módulo ───────────────────────────────────────────────────
window.exportarHorarioPDF    = exportarHorarioPDF;
window.exportarHorarioImagen = exportarHorarioImagen;