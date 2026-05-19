/**
 * NotificationPanel — HU-36
 * Gestiona la campana de notificaciones:
 *   - Conecta al stream SSE para recibir notificaciones en tiempo real
 *   - Renderiza la lista ordenada cronológicamente (más reciente primero)
 *   - Distingue visualmente notificaciones CRITICAS vs NORMALES
 *   - Implementa acciones: marcar leída, descartar, marcar todas leídas
 */

const NotificationPanel = (() => {
    const API_BASE = window.API || window.location.origin;
    let notificaciones = [];  // estado local en memoria
    let userId = null;
    let eventSource = null;/**
 * NotificationPanel — HU-36
 * Gestiona la campana de notificaciones:
 *   - Conecta al stream SSE para recibir notificaciones en tiempo real
 *   - Renderiza la lista ordenada cronológicamente (más reciente primero)
 *   - Distingue visualmente notificaciones CRITICAS vs NORMALES
 *   - Implementa acciones: marcar leída, descartar, marcar todas leídas
 *   - Patrón Command: muestra toast con botón "Deshacer" tras cada acción
 */

const NotificationPanel = (() => {
    const API_BASE = window.API || window.location.origin;
    let notificaciones = [];  // estado local en memoria
    let userId = null;
    let eventSource = null;

    // ── Inicializar ──────────────────────────────────────────────────────────

    function init() {
        const checkSession = setInterval(() => {
            const user = typeof SessionManager !== 'undefined' ? SessionManager.getInstance().getUser() : null;
            if (user && user.id) {
                clearInterval(checkSession);
                userId = user.id;
                cargarNotificaciones();
                conectarSSE();
                document.getElementById('notif-mark-all')
                    ?.addEventListener('click', marcarTodasLeidas);
            }
        }, 300);
    }

    // ── Cargar notificaciones iniciales desde la API ─────────────────────────

    async function cargarNotificaciones() {
        try {
            const res = await fetch(`${API_BASE}/api/notifications?userId=${userId}`);
            if (!res.ok) return;
            notificaciones = await res.json();
            renderizar();
        } catch (e) {
            console.error('Error cargando notificaciones:', e);
        }
    }

    // ── Conectar al canal SSE ────────────────────────────────────────────────

    function conectarSSE() {
        if (eventSource) eventSource.close();

        eventSource = new EventSource(
            `${API_BASE}/api/notifications/stream?userId=${userId}`
        );

        eventSource.addEventListener('notification', (e) => {
            const nueva = JSON.parse(e.data);
            notificaciones.unshift(nueva);
            renderizar();
            mostrarToast(nueva);
        });

        eventSource.onerror = () => {
            setTimeout(conectarSSE, 5000);
        };
    }

    // ── Renderizar la lista ──────────────────────────────────────────────────

    function renderizar() {
        const lista = document.getElementById('notification-list');
        const badge = document.getElementById('notif-badge');
        if (!lista) return;

        const noLeidas = notificaciones.filter(n => n.status === 'NO_LEIDA');

        if (noLeidas.length > 0) {
            badge.textContent = noLeidas.length;
            badge.style.display = 'inline';
        } else {
            badge.style.display = 'none';
        }

        if (notificaciones.length === 0) {
            lista.innerHTML = `<div style="padding:12px; color:#90A4AE; font-size:13px;">
                No tienes notificaciones.</div>`;
            return;
        }

        lista.innerHTML = notificaciones.map(n => crearItemHTML(n)).join('');

        notificaciones.forEach(n => {
            document.getElementById(`notif-item-${n.id}`)
                ?.addEventListener('click', (e) => {
                    if (e.target.tagName.toLowerCase() === 'button') return;
                    marcarLeida(n.id);
                    ejecutarRedireccion(n);
                });

            document.getElementById(`notif-read-${n.id}`)
                ?.addEventListener('click', (e) => {
                    e.stopPropagation();
                    marcarLeida(n.id);
                });
            document.getElementById(`notif-del-${n.id}`)
                ?.addEventListener('click', (e) => {
                    e.stopPropagation();
                    descartar(n.id);
                });
        });
    }

    function crearItemHTML(n) {
        const esCritica  = n.priority === 'CRITICA';
        const esLeida    = n.status !== 'NO_LEIDA';
        const fecha      = new Date(n.createdAt).toLocaleString('es-CO', {
            day: '2-digit', month: 'short', hour: '2-digit', minute: '2-digit'
        });

        const borderColor = esCritica ? '#E53935' : '#42A5F5';
        const bgColor     = esLeida   ? '#F5F5F5' : '#FFFFFF';
        const iconoAlert  = esCritica
            ? `<span class="material-icons-round" style="color:#E53935;font-size:18px;margin-right:6px;">warning</span>`
            : '';

        const botonLeer = !esLeida
            ? `<button id="notif-read-${n.id}" style="
                background:none; border:1px solid #90A4AE; border-radius:4px;
                padding:3px 8px; font-size:11px; cursor:pointer; margin-right:4px; color:#546E7A;">
                ✓ Leída</button>`
            : '';

        return `
        <div class="notification-item" id="notif-item-${n.id}" style="
            border-left: 3px solid ${borderColor};
            background: ${bgColor};
            opacity: ${esLeida ? '0.7' : '1'};
            cursor: pointer;
            padding: 10px 12px; margin-bottom: 4px; border-radius: 4px;
            transition: background-color 0.2s ease;">
            <div style="display:flex; align-items:flex-start; gap:6px;">
                ${iconoAlert}
                <div style="flex:1;">
                    <div class="notification-text" style="font-size:13px; color:#37474F; margin-bottom:4px;">
                        ${n.message}
                    </div>
                    <div class="notification-time" style="font-size:11px; color:#90A4AE; margin-bottom:6px;">
                        ${fecha} · <em>${n.type}</em>
                    </div>
                    <div class="notif-actions" style="position:relative; z-index:2;">
                        ${botonLeer}
                        <button id="notif-del-${n.id}" style="
                            background:none; border:1px solid #EF9A9A; border-radius:4px;
                            padding:3px 8px; font-size:11px; cursor:pointer; color:#E53935;">
                            ✕</button>
                    </div>
                </div>
            </div>
        </div>`;
    }

    // ── Acciones ─────────────────────────────────────────────────────────────

    function ejecutarRedireccion(n) {
        if (n.actionUrl && n.actionUrl.trim() !== '') {
            window.location.href = n.actionUrl;
            return;
        }

        let navId = 'nav-dashboard';

        switch (n.type) {
            case 'MENSAJE':     navId = 'nav-dashboard'; break;
            case 'TAREA':       navId = 'nav-tasks';     break;
            case 'CALENDARIO':  navId = 'nav-horario';   break;
            case 'CALIFICACION':navId = 'nav-resenas';   break;
            case 'SISTEMA':
            default:            navId = 'nav-dashboard'; break;
        }

        const navElement = document.getElementById(navId);
        if (navElement) {
            navElement.click();
            const notifDropdown = document.getElementById('notification-dropdown');
            if (notifDropdown && notifDropdown.style.display !== 'none') {
                notifDropdown.style.display = 'none';
            }
        } else {
            mostrarToast({
                priority: 'NORMAL',
                type: 'SISTEMA',
                message: 'El contenido asociado ya no está disponible.'
            });
            document.getElementById('nav-dashboard')?.click();
        }
    }

    async function marcarLeida(id) {
        try {
            const res = await fetch(
                `${API_BASE}/api/notifications/${id}/read?userId=${userId}`,
                { method: 'PATCH' }
            );
            if (res.ok) {
                const data = await res.json();
                const idx = notificaciones.findIndex(n => n.id === id);
                if (idx !== -1) notificaciones[idx].status = 'LEIDA';
                renderizar();

                // Toast con opción Deshacer
                if (data.undoDisponible) {
                    mostrarToastUndo(data.mensaje, async () => {
                        await ejecutarUndo();
                        // Revertir visualmente en estado local
                        const i = notificaciones.findIndex(n => n.id === id);
                        if (i !== -1) notificaciones[i].status = 'NO_LEIDA';
                        renderizar();
                    });
                }
            }
        } catch (e) { console.error('Error marcando leída:', e); }
    }

    async function descartar(id) {
        try {
            const res = await fetch(
                `${API_BASE}/api/notifications/${id}?userId=${userId}`,
                { method: 'DELETE' }
            );
            if (res.ok) {
                const data = await res.json();
                // Guardar copia local antes de quitar del array
                const notifEliminada = notificaciones.find(n => n.id === id);
                notificaciones = notificaciones.filter(n => n.id !== id);
                renderizar();

                // Toast con opción Deshacer
                if (data.undoDisponible) {
                    mostrarToastUndo(data.mensaje, async () => {
                        await ejecutarUndo();
                        // Recargar lista desde servidor para obtener nuevo ID
                        await cargarNotificaciones();
                    });
                }
            }
        } catch (e) { console.error('Error descartando notificación:', e); }
    }

    async function marcarTodasLeidas() {
        const noLeidas = notificaciones.filter(n => n.status === 'NO_LEIDA');
        await Promise.all(noLeidas.map(n => marcarLeida(n.id)));
    }

    // ── Undo ─────────────────────────────────────────────────────────────────

    async function ejecutarUndo() {
        try {
            const res = await fetch(
                `${API_BASE}/api/notifications/undo?userId=${userId}`,
                { method: 'POST' }
            );
            if (!res.ok) console.error('Error al deshacer acción');
        } catch (e) {
            console.error('Error ejecutando undo:', e);
        }
    }

    // ── Toast de notificación nueva (informativo) ─────────────────────────────

    function mostrarToast(n) {
        const toast = document.createElement('div');
        toast.style.cssText = `
            position:fixed; bottom:24px; right:24px; z-index:9999;
            background:${n.priority === 'CRITICA' ? '#E53935' : '#323232'};
            color:#fff; padding:12px 18px; border-radius:8px;
            font-size:13px; max-width:300px; box-shadow:0 4px 12px rgba(0,0,0,0.2);
            animation: fadeIn 0.3s ease;`;
        toast.innerHTML = `
            <strong>${n.priority === 'CRITICA' ? '⚠ ' : ''}${n.type}</strong><br>
            ${n.message}`;
        document.body.appendChild(toast);
        setTimeout(() => toast.remove(), 4000);
    }

    // ── Toast con botón Deshacer (patrón Command) ─────────────────────────────

    /**
     * Muestra un toast que incluye un botón "Deshacer".
     * Desaparece automáticamente en 5 s o al pulsar Deshacer.
     *
     * @param {string}   mensaje  Texto descriptivo de la acción realizada.
     * @param {Function} onUndo   Callback asíncrono a ejecutar al pulsar Deshacer.
     */
    function mostrarToastUndo(mensaje, onUndo) {
        // Eliminar toast anterior si existe (evita acumulación)
        document.getElementById('notif-toast-undo')?.remove();

        const toast = document.createElement('div');
        toast.id = 'notif-toast-undo';
        toast.style.cssText = `
            position: fixed; bottom: 24px; right: 24px; z-index: 10000;
            background: #323232; color: #fff;
            padding: 12px 16px; border-radius: 8px;
            font-size: 13px; max-width: 320px;
            box-shadow: 0 4px 16px rgba(0,0,0,0.25);
            display: flex; align-items: center; gap: 12px;
            animation: fadeIn 0.25s ease;`;

        toast.innerHTML = `
            <span style="flex:1;">${mensaje}</span>
            <button id="notif-undo-btn" style="
                background: none; border: 1px solid #90CAF9;
                color: #90CAF9; border-radius: 4px;
                padding: 4px 10px; font-size: 12px;
                cursor: pointer; white-space: nowrap;">
                Deshacer
            </button>`;

        document.body.appendChild(toast);

        // Auto-desaparecer en 5 s
        const timer = setTimeout(() => toast.remove(), 5000);

        // Acción al pulsar Deshacer
        toast.querySelector('#notif-undo-btn').addEventListener('click', async () => {
            clearTimeout(timer);
            toast.remove();
            await onUndo();
        });
    }

    return { init };
})();

// Auto-inicializar cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', () => NotificationPanel.init());

    // ── Inicializar ──────────────────────────────────────────────────────────

    function init() {
        // Esperar a que la sesión esté disponible
        const checkSession = setInterval(() => {
            const user = typeof SessionManager !== 'undefined' ? SessionManager.getInstance().getUser() : null;
            if (user && user.id) {
                clearInterval(checkSession);
                userId = user.id;
                cargarNotificaciones();
                conectarSSE();
                document.getElementById('notif-mark-all')
                    ?.addEventListener('click', marcarTodasLeidas);
            }
        }, 300);
    }

    // ── Cargar notificaciones iniciales desde la API ─────────────────────────

    async function cargarNotificaciones() {
        try {
            const res = await fetch(`${API_BASE}/api/notifications?userId=${userId}`);
            if (!res.ok) return;
            notificaciones = await res.json();
            renderizar();
        } catch (e) {
            console.error('Error cargando notificaciones:', e);
        }
    }

    // ── Conectar al canal SSE ────────────────────────────────────────────────

    function conectarSSE() {
        if (eventSource) eventSource.close();

        eventSource = new EventSource(
            `${API_BASE}/api/notifications/stream?userId=${userId}`
        );

        eventSource.addEventListener('notification', (e) => {
            const nueva = JSON.parse(e.data);
            // Insertar al inicio (más reciente primero)
            notificaciones.unshift(nueva);
            renderizar();
            mostrarToast(nueva);
        });

        eventSource.onerror = () => {
            // Reconectar automáticamente después de 5 s
            setTimeout(conectarSSE, 5000);
        };
    }

    // ── Renderizar la lista ──────────────────────────────────────────────────

    function renderizar() {
        const lista = document.getElementById('notification-list');
        const badge = document.getElementById('notif-badge');
        if (!lista) return;

        const noLeidas = notificaciones.filter(n => n.status === 'NO_LEIDA');

        // Actualizar badge
        if (noLeidas.length > 0) {
            badge.textContent = noLeidas.length;
            badge.style.display = 'inline';
        } else {
            badge.style.display = 'none';
        }

        if (notificaciones.length === 0) {
            lista.innerHTML = `<div style="padding:12px; color:#90A4AE; font-size:13px;">
                No tienes notificaciones.</div>`;
            return;
        }

        lista.innerHTML = notificaciones.map(n => crearItemHTML(n)).join('');

        // Adjuntar eventos a los elementos generados
        notificaciones.forEach(n => {
            // Evento para el contenedor principal de la notificación
            document.getElementById(`notif-item-${n.id}`)
                ?.addEventListener('click', (e) => {
                    // Evitar que botones hijos disparen este evento
                    if (e.target.tagName.toLowerCase() === 'button') return;
                    marcarLeida(n.id);
                    ejecutarRedireccion(n);
                });

            document.getElementById(`notif-read-${n.id}`)
                ?.addEventListener('click', (e) => { 
                    e.stopPropagation(); 
                    marcarLeida(n.id); 
                });
            document.getElementById(`notif-del-${n.id}`)
                ?.addEventListener('click', (e) => { 
                    e.stopPropagation(); 
                    descartar(n.id); 
                });
        });
    }

    function crearItemHTML(n) {
        const esCritica  = n.priority === 'CRITICA';
        const esLeida    = n.status !== 'NO_LEIDA';
        const fecha      = new Date(n.createdAt).toLocaleString('es-CO', {
            day: '2-digit', month: 'short', hour: '2-digit', minute: '2-digit'
        });

        // Estilo de borde según prioridad
        const borderColor = esCritica ? '#E53935' : '#42A5F5';
        const bgColor     = esLeida   ? '#F5F5F5' : '#FFFFFF';
        const iconoAlert  = esCritica
            ? `<span class="material-icons-round" style="color:#E53935;font-size:18px;margin-right:6px;">warning</span>`
            : '';

        const botonLeer = !esLeida
            ? `<button id="notif-read-${n.id}" style="
                background:none; border:1px solid #90A4AE; border-radius:4px;
                padding:3px 8px; font-size:11px; cursor:pointer; margin-right:4px; color:#546E7A;">
                ✓ Leída</button>`
            : '';

        return `
        <div class="notification-item" id="notif-item-${n.id}" style="
            border-left: 3px solid ${borderColor};
            background: ${bgColor};
            opacity: ${esLeida ? '0.7' : '1'};
            cursor: pointer;
            padding: 10px 12px; margin-bottom: 4px; border-radius: 4px;
            transition: background-color 0.2s ease;">
            <div style="display:flex; align-items:flex-start; gap:6px;">
                ${iconoAlert}
                <div style="flex:1;">
                    <div class="notification-text" style="font-size:13px; color:#37474F; margin-bottom:4px;">
                        ${n.message}
                    </div>
                    <div class="notification-time" style="font-size:11px; color:#90A4AE; margin-bottom:6px;">
                        ${fecha} · <em>${n.type}</em>
                    </div>
                    <div class="notif-actions" style="position:relative; z-index:2;">
                        ${botonLeer}
                        <button id="notif-del-${n.id}" style="
                            background:none; border:1px solid #EF9A9A; border-radius:4px;
                            padding:3px 8px; font-size:11px; cursor:pointer; color:#E53935;">
                            ✕</button>
                    </div>
                </div>
            </div>
        </div>`;
    }

    // ── Acciones ─────────────────────────────────────────────────────────────

    function ejecutarRedireccion(n) {
        // Redirección directa si hay URL válida
        if (n.actionUrl && n.actionUrl.trim() !== '') {
            window.location.href = n.actionUrl;
            return;
        }

        let navId = 'nav-dashboard'; // Por defecto (Escenario 4)

        switch (n.type) {
            case 'MENSAJE':
                // Escenario 1: Bandeja de entrada (Actualmente no existe, va al dashboard)
                navId = 'nav-dashboard';
                break;
            case 'TAREA':
                // Escenario 2: Vista detallada de la tarea
                navId = 'nav-tasks';
                break;
            case 'CALENDARIO':
                // Escenario 3: Vista de evento de calendario
                navId = 'nav-horario';
                break;
            case 'CALIFICACION':
                navId = 'nav-resenas';
                break;
            case 'SISTEMA':
            default:
                // Escenario 4: Dashboard
                navId = 'nav-dashboard';
                break;
        }

        const navElement = document.getElementById(navId);
        if (navElement) {
            navElement.click();
            
            // Cerrar el contenedor de notificaciones si está abierto
            const notifDropdown = document.getElementById('notification-dropdown');
            if (notifDropdown && notifDropdown.style.display !== 'none') {
                notifDropdown.style.display = 'none';
            }
        } else {
            // Escenario 5: Contenido no disponible / Fallback
            mostrarToast({
                priority: 'NORMAL',
                type: 'SISTEMA',
                message: 'El contenido asociado ya no está disponible.'
            });
            document.getElementById('nav-dashboard')?.click();
        }
    }

    async function marcarLeida(id) {
        try {
            const res = await fetch(`${API_BASE}/api/notifications/${id}/read`, {
                method: 'PATCH'
            });
            if (res.ok) {
                const idx = notificaciones.findIndex(n => n.id === id);
                if (idx !== -1) notificaciones[idx].status = 'LEIDA';
                renderizar();
            }
        } catch (e) { console.error('Error marcando leída:', e); }
    }

    async function descartar(id) {
        try {
            const res = await fetch(`${API_BASE}/api/notifications/${id}`, {
                method: 'DELETE'
            });
            if (res.ok) {
                notificaciones = notificaciones.filter(n => n.id !== id);
                renderizar();
            }
        } catch (e) { console.error('Error descartando notificación:', e); }
    }

    async function marcarTodasLeidas() {
        const noLeidas = notificaciones.filter(n => n.status === 'NO_LEIDA');
        await Promise.all(noLeidas.map(n => marcarLeida(n.id)));
    }

    // ── Toast de notificación nueva ──────────────────────────────────────────

    function mostrarToast(n) {
        const toast = document.createElement('div');
        toast.style.cssText = `
            position:fixed; bottom:24px; right:24px; z-index:9999;
            background:${n.priority === 'CRITICA' ? '#E53935' : '#323232'};
            color:#fff; padding:12px 18px; border-radius:8px;
            font-size:13px; max-width:300px; box-shadow:0 4px 12px rgba(0,0,0,0.2);
            animation: fadeIn 0.3s ease;`;
        toast.innerHTML = `
            <strong>${n.priority === 'CRITICA' ? '⚠ ' : ''}${n.type}</strong><br>
            ${n.message}`;
        document.body.appendChild(toast);
        setTimeout(() => toast.remove(), 4000);
    }

    return { init };
})();

// Auto-inicializar cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', () => NotificationPanel.init());


