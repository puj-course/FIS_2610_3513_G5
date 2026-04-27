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
 * detectarConflictos(sesiones)
 * Agrupa las sesiones por día y detecta solapamientos de horario.
 * Devuelve un Map: clave = "dia-horaInicio" → índice de columna dentro del conflicto.
 *
 * Ejemplo: si Cálculo y Física se solapan el Lunes,
 * Cálculo queda en columna 0 de 2 y Física en columna 1 de 2.
 *
 * @param {Array} sesiones - Array de horarioDTO
 * @returns {Map} conflictos: key = sesion → { col, totalCols }
 */
function detectarConflictos(sesiones) {
    // Resultado: Map de sesión → { col, totalCols }
    const resultado = new Map();

    // Agrupar sesiones por columna de día
    const porDia = {};
    sesiones.forEach(s => {
        const diaKey = s.dia?.toLowerCase().trim();
        const col = DIA_A_COL[diaKey];
        if (!col) return;
        if (!porDia[col]) porDia[col] = [];
        porDia[col].push(s);
    });

    // Para cada día, encontrar grupos de sesiones que se solapan
    Object.values(porDia).forEach(sesionesDelDia => {
        // Ordenar por hora de inicio
        sesionesDelDia.sort((a, b) => horaADecimal(a.horaInicio) - horaADecimal(b.horaInicio));

        // Construir grupos de solapamiento
        // Un grupo es un conjunto donde cada sesión solapa con al menos una del grupo
        const visitados = new Set();

        sesionesDelDia.forEach((s, i) => {
            if (visitados.has(i)) return;

            const grupo = [i];
            visitados.add(i);

            const finI = horaADecimal(s.horaFin);

            // Comparar con todas las siguientes
            for (let j = i + 1; j < sesionesDelDia.length; j++) {
                const sj = sesionesDelDia[j];
                const inicioJ = horaADecimal(sj.horaInicio);

                // Hay solapamiento si el inicio de j es menor que el fin de i
                if (inicioJ < finI) {
                    grupo.push(j);
                    visitados.add(j);
                }
            }

            // Asignar posición dentro del grupo a cada sesión
            grupo.forEach((idx, posEnGrupo) => {
                resultado.set(sesionesDelDia[idx], {
                    col:       posEnGrupo,
                    totalCols: grupo.length
                });
            });
        });
    });

    return resultado;
}

/**
 * renderBloqueMateria(sesion, conflictoInfo)
 * Crea y posiciona un bloque de materia en la celda correcta.
 * Si hay conflicto, divide el ancho de la celda entre los bloques solapados.
 *
 * @param {Object} sesion        - horarioDTO: { nombreAsignatura, dia, horaInicio, horaFin, profesor }
 * @param {Object} conflictoInfo - { col, totalCols } desde detectarConflictos()
 */
function renderBloqueMateria(sesion, conflictoInfo) {
    const diaKey = sesion.dia?.toLowerCase().trim();
    const colIdx = DIA_A_COL[diaKey];
    if (!colIdx) return;

    const hInicio  = horaADecimal(sesion.horaInicio);
    const hFin     = horaADecimal(sesion.horaFin);
    const filaBase = Math.floor(hInicio);

    const celdaAncla = document.getElementById(`celda-${filaBase}-${colIdx}`);
    if (!celdaAncla) return;

    const PX_POR_HORA = 52;
    const offsetTop   = (hInicio - filaBase) * PX_POR_HORA;
    const duracion    = Math.max(hFin - hInicio, 0.5);
    const altura      = duracion * PX_POR_HORA;

    // ── Manejo de conflictos: dividir ancho ──
    const totalCols  = conflictoInfo?.totalCols ?? 1;
    const colBloque  = conflictoInfo?.col        ?? 0;
    const anchoPct   = 100 / totalCols;          // % del ancho que ocupa este bloque
    const leftPct    = anchoPct * colBloque;     // % desde la izquierda

    const color = obtenerColor(sesion.nombreAsignatura);

    const bloque = document.createElement('div');
    bloque.className = 'horario-bloque';
    bloque.title = `${sesion.nombreAsignatura}\n${sesion.horaInicio}–${sesion.horaFin}${sesion.profesor ? '\n' + sesion.profesor : ''}`;

    // Indicador visual de conflicto: borde punteado cuando hay solapamiento
    const esConflicto = totalCols > 1;

    bloque.style.cssText = `
        position: absolute;
        top: ${offsetTop}px;
        left: calc(${leftPct}% + 2px);
        width: calc(${anchoPct}% - 4px);
        height: ${altura - 2}px;
        background: ${color.bg};
        border-left: 3px solid ${color.border};
        border-radius: 4px;
        padding: 3px 5px;
        overflow: hidden;
        z-index: ${1 + colBloque};
        cursor: default;
        box-shadow: 0 1px 3px rgba(0,0,0,0.08);
        ${esConflicto ? `outline: 1.5px dashed ${color.border};` : ''}
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
        ${esConflicto ? `<span style="
            display: block;
            font-size: 8px;
            color: #E53935;
            margin-top: 2px;
            font-weight: 600;
        ">⚠ Conflicto</span>` : ''}
    `;

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
    const emptyState  = document.getElementById('horario-empty');
    const gridWrapper = document.getElementById('horario-grid-wrapper');

    // Limpiar bloques previos
    document.querySelectorAll('.horario-bloque').forEach(b => b.remove());

    // Resetear colores para que coincidan en cada recarga
    Object.keys(colorPorMateria).forEach(k => delete colorPorMateria[k]);
    colorIndex = 0;

    if (!sesiones || sesiones.length === 0) {
        if (emptyState)  emptyState.style.display  = 'flex';
        if (gridWrapper) gridWrapper.style.display  = 'none';
        return;
    }

    if (emptyState)  emptyState.style.display  = 'none';
    if (gridWrapper) gridWrapper.style.display  = '';

    // Detectar conflictos antes de pintar para ajustar anchos
    const conflictos = detectarConflictos(sesiones);
    sesiones.forEach(sesion => renderBloqueMateria(sesion, conflictos.get(sesion)));
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