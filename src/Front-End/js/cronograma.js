/**
 * cronograma.js - HU-35: Horario Semanal Expandido
 * Maneja la visualización, edición y exportación del cronograma.
 */
const CronogramaManager = (() => {
    const API_BASE = (typeof API !== 'undefined') ? API : 'http://localhost:8080/api';
    let asignaciones = [];
    let fechaActual = '2026-05-11'; // Por defecto, una semana de ejemplo

    // --- Inicialización ---
    function init() {
        const navBtn = document.getElementById('nav-cronograma');
        if (navBtn) {
            navBtn.addEventListener('click', () => {
                mostrarVista();
                cargarDatos();
            });
        }
    }

    function mostrarVista() {
        if (typeof hideAllViews === 'function') hideAllViews();
        document.getElementById('cronograma-view').classList.add('active');
        if (typeof syncNavbar === 'function') syncNavbar('nav-cronograma');
    }

    async function cargarDatos() {
        try {
            const res = await fetch(`${API_BASE}/cronogramas?fecha=${fechaActual}`);
            asignaciones = await res.json();
            renderizar();
        } catch (e) {
            console.error("Error cargando cronograma:", e);
        }
    }

    // --- Renderizado (#478) ---
    function renderizar() {
        const grid = document.getElementById('tabla-cronograma-body');
        if (!grid) return;

        // Filtro por rol (#479): Si el usuario es regular, filtrar solo sus asignaciones
        const user = (typeof session !== 'undefined') ? session.getUser() : null;
        let listaMostrar = asignaciones;
        if (user && user.rol !== 'ADMIN') {
            listaMostrar = asignaciones.filter(a => a.usuario.id === user.id);
        }

        grid.innerHTML = listaMostrar.map(a => `
            <tr class="${a.tieneConflicto ? 'fila-conflicto' : ''}">
                <td>${a.usuario.nombre} ${a.usuario.apellido}</td>
                <td>${a.fecha}</td>
                <td class="editable-cell" contenteditable="true" onblur="CronogramaManager.actualizar(${a.id}, 'proyecto', this)">
                    ${a.proyecto}
                </td>
                <td>${a.turno.nombre} (${a.turno.horaInicio}-${a.turno.horaFin})</td>
                <td class="editable-cell" contenteditable="true" onblur="CronogramaManager.actualizar(${a.id}, 'horasDiarias', this)">
                    ${a.horasDiarias}
                </td>
                <td>
                    ${a.tieneConflicto ? '<span class="conflicto-badge">⚠️ Conflicto</span>' : '✅ OK'}
                </td>
            </tr>
        `).join('');
    }

    // --- Edición Inline (#479) ---
    async function actualizar(id, campo, elemento) {
        const valor = elemento.innerText.trim();
        const asignacionOriginal = asignaciones.find(a => a.id === id);
        if (!asignacionOriginal) return;

        const body = { ...asignacionOriginal };
        body[campo] = (campo === 'horasDiarias') ? parseInt(valor) : valor;

        try {
            const res = await fetch(`${API_BASE}/cronogramas/asignaciones/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(body)
            });
            if (res.ok) {
                const actualizada = await res.json();
                // Actualizar localmente y re-renderizar para mostrar conflictos si los hay
                const idx = asignaciones.findIndex(a => a.id === id);
                asignaciones[idx] = actualizada;
                renderizar();
            }
        } catch (e) {
            console.error("Error al actualizar:", e);
        }
    }

    // --- Exportación (#480) ---
    function exportar(formato) {
        window.open(`${API_BASE}/cronogramas/export?fecha=${fechaActual}&formato=${formato}`, '_blank');
    }

    return { init, actualizar, exportar };
})();

document.addEventListener('DOMContentLoaded', CronogramaManager.init);
