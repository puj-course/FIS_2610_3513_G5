/**
 * horario.js — HU-30: Horario Semanal
 * Sub Issue 2: Grilla base (estructura, franjas horarias, navegación)
 */

// ── Constantes ─────────────────────────────────────────────────────────────
const HORA_INICIO  = 6;   // 6:00
const HORA_FIN     = 22;  // 22:00 (última fila visible)
const DIAS_SEMANA  = ['Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado'];

// Índice de cada día en la grilla (columna 0 = hora, col 1 = Lunes, ...)
const DIA_A_COL = {
    'lunes':      1,
    'martes':     2,
    'miércoles':  3,
    'miercoles':  3,
    'jueves':     4,
    'viernes':    5,
    'sábado':     6,
    'sabado':     6
};

// ── Construcción de la grilla base ─────────────────────────────────────────

/**
 * generarFilasHorario()
 * Inserta en #horario-grid las filas de hora vacías (6:00 – 22:00).
 * Cada fila: 1 celda-hora + 6 celdas-día con id = "celda-{hora}-{diaIdx}"
 */
function generarFilasHorario() {
    const grid = document.getElementById('horario-grid');
    if (!grid) return;

    // Eliminar filas previas (excepto la cabecera: primeros 7 hijos)
    const celdas = grid.querySelectorAll('.horario-hora-cell, .horario-dia-cell');
    celdas.forEach(c => c.remove());

    for (let h = HORA_INICIO; h <= HORA_FIN; h++) {
        const etiqueta = `${h}:00`;

        // Celda de hora
        const celdaHora = document.createElement('div');
        celdaHora.className = 'horario-hora-cell';
        celdaHora.textContent = etiqueta;
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

// ── Navegación ─────────────────────────────────────────────────────────────

/**
 * initNavHorario()
 * Registra el listener del nav-item para abrir la vista de horario.
 * Llama a generarFilasHorario() la primera vez que se abre.
 */
function initNavHorario() {
    const navBtn      = document.getElementById('nav-horario');
    const horarioView = document.getElementById('horario-view');
    if (!navBtn || !horarioView) return;

    let grilaGenerada = false;

    navBtn.addEventListener('click', () => {
        // Reutiliza hideAllViews() definida en el script principal
        if (typeof hideAllViews === 'function') hideAllViews();

        horarioView.classList.add('active');

        if (typeof syncNavbar === 'function') syncNavbar('nav-horario');

        // Ocultar sidebar derecho (igual que tasks/subjects)
        const sidebarRight = document.querySelector('.sidebar-right');
        if (sidebarRight) sidebarRight.style.display = 'none';
        const appContainer = document.querySelector('.app-container');
        if (appContainer) appContainer.style.gridTemplateColumns = '240px 1fr';

        // Generar filas solo la primera vez
        if (!grilaGenerada) {
            generarFilasHorario();
            grilaGenerada = true;
        }
    });
}

// ── Init ───────────────────────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', initNavHorario);


