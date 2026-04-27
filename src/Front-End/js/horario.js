/**
 * horario.js — HU-30: Horario Semanal
 * Sub Issue 2: Grilla base
 * Sub Issue 3: Renderizado de bloques de materia
 */

// ── Constantes ─────────────────────────────────────────────────────────────
const HORA_INICIO = 6;    // Primera fila visible: 6:00
const HORA_FIN    = 22;   // Última fila visible:  22:00
const DIAS_SEMANA = ['Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado'];

// Mapea el nombre del día (en cualquier capitalización) a su columna (1–6)
const DIA_A_COL = {
    'lunes':     1,
    'martes':    2,
    'miércoles': 3,
    'miercoles': 3,
    'jueves':    4,
    'viernes':   5,
    'sábado':    6,
    'sabado':    6
};

// Paleta de colores para diferenciar materias visualmente
const COLORES_MATERIA = [
    { bg: '#E3F2FD', border: '#1E88E5', text: '#0D47A1' },
    { bg: '#E8F5E9', border: '#43A047', text: '#1B5E20' },
    { bg: '#FFF3E0', border: '#FB8C00', text: '#E65100' },
    { bg: '#F3E5F5', border: '#8E24AA', text: '#4A148C' },
    { bg: '#E0F7FA', border: '#00ACC1', text: '#006064' },
    { bg: '#FCE4EC', border: '#E53935', text: '#B71C1C' },
    { bg: '#F9FBE7', border: '#C0CA33', text: '#827717' },
    { bg: '#EDE7F6', border: '#5E35B1', text: '#311B92' },
];

// Mapa nombre-materia → índice de color (se asigna la primera vez que aparece)
const colorPorMateria = {};
let colorIndex = 0;

// ── Utilidades ─────────────────────────────────────────────────────────────

/**
 * Convierte "HH:MM" a número de horas decimales.
 * Ej: "08:30" → 8.5
 */
function horaADecimal(horaStr) {
    if (!horaStr) return 0;
    const [h, m] = horaStr.split(':').map(Number);
    return h + (m || 0) / 60;
}

/**
 * Devuelve el objeto de color asignado a una materia.
 * Si es la primera vez que aparece esa materia, le asigna el siguiente color.
 */
function obtenerColor(nombreMateria) {
    if (colorPorMateria[nombreMateria] === undefined) {
        colorPorMateria[nombreMateria] = colorIndex % COLORES_MATERIA.length;
        colorIndex++;
    }
    return COLORES_MATERIA[colorPorMateria[nombreMateria]];
}

// ── Construcción de la grilla base ─────────────────────────────────────────

/**
 * Inserta en #horario-grid las filas de hora vacías (HORA_INICIO–HORA_FIN).
 * Cada fila: 1 celda-hora + 6 celdas-día con id="celda-{h}-{diaIdx}"
 */
function generarFilasHorario() {
    const grid = document.getElementById('horario-grid');
    if (!grid) return;

    // Limpiar filas previas (mantiene los 7 header-cells)
    grid.querySelectorAll('.horario-hora-cell, .horario-dia-cell').forEach(c => c.remove());

    for (let h = HORA_INICIO; h <= HORA_FIN; h++) {
        // Celda de hora
        const celdaHora = document.createElement('div');
        celdaHora.className = 'horario-hora-cell';
        celdaHora.textContent = `${h}:00`;
        grid.appendChild(celdaHora);

        // 6 celdas de día
        for (let d = 1; d <= 6; d++) {
            const celda = document.createElement('div');
            celda.className = 'horario-dia-cell';
            celda.id = `celda-${h}-${d}`;
            grid.appendChild(celda);
        }
    }
}

// ── Renderizado de bloques ──────────────────────────────────────────────────

/**
 * renderBloqueMateria(sesion)
 * Crea y posiciona un bloque de materia en la celda correcta.
 *
 * @param {Object} sesion - horarioDTO del backend:
 *   { nombreAsignatura, dia, horaInicio, horaFin, profesor }
 */
function renderBloqueMateria(sesion) {
    const diaKey = sesion.dia?.toLowerCase().trim();
    const colIdx = DIA_A_COL[diaKey];
    if (!colIdx) return; // día no reconocido

    const hInicio  = horaADecimal(sesion.horaInicio);
    const hFin     = horaADecimal(sesion.horaFin);
    const filaBase = Math.floor(hInicio); // hora entera donde empieza el bloque

    // La celda ancla es la de la hora de inicio
    const celdaAncla = document.getElementById(`celda-${filaBase}-${colIdx}`);
    if (!celdaAncla) return;

    // Calcular posición y altura relativas a la hora de inicio
    // Cada celda = 52px (height definido en CSS: .horario-dia-cell { height: 52px })
    const PX_POR_HORA = 52;
    const offsetTop    = (hInicio - filaBase) * PX_POR_HORA;
    const duracion     = Math.max(hFin - hInicio, 0.5); // mínimo medio bloque
    const altura       = duracion * PX_POR_HORA;

    const color = obtenerColor(sesion.nombreAsignatura);

    const bloque = document.createElement('div');
    bloque.className = 'horario-bloque';
    bloque.title = `${sesion.nombreAsignatura}\n${sesion.horaInicio}–${sesion.horaFin}${sesion.profesor ? '\n' + sesion.profesor : ''}`;

    bloque.style.cssText = `
        position: absolute;
        top: ${offsetTop}px;
        left: 2px;
        right: 2px;
        height: ${altura - 2}px;
        background: ${color.bg};
        border-left: 3px solid ${color.border};
        border-radius: 4px;
        padding: 3px 5px;
        overflow: hidden;
        z-index: 1;
        cursor: default;
        box-shadow: 0 1px 3px rgba(0,0,0,0.08);
    `;

    bloque.innerHTML = `
        <span style="
            display: block;
            font-size: 10px;
            font-weight: 700;
            color: ${color.text};
            line-height: 1.3;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
        ">${sesion.nombreAsignatura}</span>
        <span style="
            display: block;
            font-size: 9px;
            color: ${color.border};
            margin-top: 1px;
        ">${sesion.horaInicio}–${sesion.horaFin}</span>
    `;

    // La celda ancla necesita position:relative para que el bloque se posicione dentro
    celdaAncla.style.position = 'relative';
    celdaAncla.appendChild(bloque);
}

/**
 * renderHorario(sesiones)
 * Recibe el array de horarioDTOs y pinta todos los bloques.
 * Muestra el estado vacío si no hay sesiones con horario.
 *
 * @param {Array} sesiones - Array de horarioDTO
 */
function renderHorario(sesiones) {
    const emptyState   = document.getElementById('horario-empty');
    const gridWrapper  = document.getElementById('horario-grid-wrapper');

    // Limpiar bloques previos
    document.querySelectorAll('.horario-bloque').forEach(b => b.remove());
    // Resetear colores para que coincidan en cada recarga
    Object.keys(colorPorMateria).forEach(k => delete colorPorMateria[k]);
    colorIndex = 0;

    if (!sesiones || sesiones.length === 0) {
        // Estado vacío
        if (emptyState)  emptyState.style.display  = 'flex';
        if (gridWrapper) gridWrapper.style.display  = 'none';
        return;
    }

    // Hay datos: mostrar grilla
    if (emptyState)  emptyState.style.display  = 'none';
    if (gridWrapper) gridWrapper.style.display  = '';

    sesiones.forEach(sesion => renderBloqueMateria(sesion));
}

// ── Fetch de datos ──────────────────────────────────────────────────────────

/**
 * fetchHorarioBackend()
 * Consulta GET /asignaturas/horario?usuarioId=X y llama a renderHorario().
 */
async function fetchHorarioBackend() {
    try {
        // API y getUserId() están definidos globalmente en index.html
        const userId = typeof getUserId === 'function' ? getUserId() : null;
        if (!userId) {
            renderHorario([]);
            return;
        }

        const res = await fetch(`${API}/asignaturas/horario?usuarioId=${userId}`);
        if (!res.ok) throw new Error('Error al cargar el horario');

        const sesiones = await res.json();
        renderHorario(sesiones);

    } catch (err) {
        console.error('[Horario] Error:', err);
        renderHorario([]);
    }
}

// ── Navegación ─────────────────────────────────────────────────────────────

/**
 * initNavHorario()
 * Registra el listener del nav-item "Horario Semanal".
 */
function initNavHorario() {
    const navBtn      = document.getElementById('nav-horario');
    const horarioView = document.getElementById('horario-view');
    if (!navBtn || !horarioView) return;

    let grilaGenerada = false;

    navBtn.addEventListener('click', () => {
        if (typeof hideAllViews === 'function') hideAllViews();

        horarioView.classList.add('active');
        if (typeof syncNavbar === 'function') syncNavbar('nav-horario');

        // Ocultar sidebar derecho (mismo patrón que tasks/subjects)
        const sidebarRight = document.querySelector('.sidebar-right');
        if (sidebarRight) sidebarRight.style.display = 'none';
        const appContainer = document.querySelector('.app-container');
        if (appContainer) appContainer.style.gridTemplateColumns = '240px 1fr';

        // Generar filas solo la primera vez
        if (!grilaGenerada) {
            generarFilasHorario();
            grilaGenerada = true;
        }

        // Cargar datos del backend cada vez que se abre la vista
        fetchHorarioBackend();
    });
}

// ── Init ───────────────────────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', initNavHorario);