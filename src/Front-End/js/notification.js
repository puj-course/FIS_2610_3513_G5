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
    let eventSource = null;

    // ── Inicializar ──────────────────────────────────────────────────────────

    function init() {
        // Esperar a que la sesión esté disponible
        const checkSession = setInterval(() => {
            const user = typeof session !== 'undefined' ? session.getUser() : null;
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

        // Adjuntar eventos a los botones generados
        notificaciones.forEach(n => {
            document.getElementById(`notif-read-${n.id}`)
                ?.addEventListener('click', () => marcarLeida(n.id));
            document.getElementById(`notif-del-${n.id}`)
                ?.addEventListener('click', () => descartar(n.id));
            if (n.actionUrl) {
                document.getElementById(`notif-action-${n.id}`)
                    ?.addEventListener('click', () => {
                        marcarLeida(n.id);
                        window.location.href = n.actionUrl;
                    });
            }
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

        const botonAccion = n.actionUrl
            ? `<button id="notif-action-${n.id}" style="
                background:#1A73E8; color:#fff; border:none; border-radius:4px;
                padding:3px 8px; font-size:11px; cursor:pointer; margin-right:4px;">
                Ver</button>`
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
            padding: 10px 12px; margin-bottom: 4px; border-radius: 4px;">
            <div style="display:flex; align-items:flex-start; gap:6px;">
                ${iconoAlert}
                <div style="flex:1;">
                    <div class="notification-text" style="font-size:13px; color:#37474F; margin-bottom:4px;">
                        ${n.message}
                    </div>
                    <div class="notification-time" style="font-size:11px; color:#90A4AE; margin-bottom:6px;">
                        ${fecha} · <em>${n.type}</em>
                    </div>
                    <div>
                        ${botonAccion}
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


