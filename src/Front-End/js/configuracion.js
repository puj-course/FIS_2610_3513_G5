/**
 * Lógica para la Configuración de Apariencia del Dashboard
 * (HU-31, HU-363 a HU-367)
 */

document.addEventListener('DOMContentLoaded', () => {
    const configBtn = document.getElementById('nav-config');
    const modalOverlay = document.getElementById('config-modal-overlay');
    const modalClose = document.getElementById('config-modal-close');
    const btnCancel = document.getElementById('config-btn-cancel');
    const btnSave = document.getElementById('config-btn-save');
    const btnReset = document.getElementById('config-btn-reset');
    const temaSelect = document.getElementById('config-tema');
    const swatches = document.querySelectorAll('.color-swatch');

    // Si no existen los elementos necesarios, abortar para no romper otros scripts
    if (!configBtn || !modalOverlay) {
        console.warn('[Config] No se encontraron elementos de configuración en el DOM.');
        return;
    }

    // Preferencias locales temporales (para previsualización)
    let currentPrefs = {
        tema: 'claro',
        colorPrimario: '#1E88E5'
    };

    // Preferencias guardadas en BD
    let savedPrefs = { ...currentPrefs };

    // Obtener ID del usuario actual dinámicamente
    const getActiveUserId = () => {
        const user = JSON.parse(localStorage.getItem('studyhub_user'));
        return user ? user.id : 1;
    };

    const API_URL = window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1' 
                    ? 'https://studyhub-c2ft.onrender.com/api' 
                    : window.location.origin + '/api';

    // Cargar preferencias iniciales
    cargarPreferenciasBD();

    // Event Listeners - Abrir/Cerrar
    configBtn.addEventListener('click', () => {
        // Restaurar estado del modal a las preferencias guardadas
        currentPrefs = { ...savedPrefs };
        actualizarUIModal();
        modalOverlay.style.display = 'flex';
    });

    const cerrarModal = () => {
        modalOverlay.style.display = 'none';
        // Deshacer previsualización si se cancela
        aplicarPreferencias(savedPrefs);
    };

    modalClose.addEventListener('click', cerrarModal);
    btnCancel.addEventListener('click', cerrarModal);

    // Event Listener - Cambio de Tema (Previsualización)
    temaSelect.addEventListener('change', (e) => {
        currentPrefs.tema = e.target.value;
        aplicarPreferencias(currentPrefs);
    });

    // Event Listener - Cambio de Color (Previsualización)
    swatches.forEach(swatch => {
        swatch.addEventListener('click', (e) => {
            const color = e.target.getAttribute('data-color');
            currentPrefs.colorPrimario = color;
            
            // Actualizar clase activa
            swatches.forEach(s => s.classList.remove('active'));
            e.target.classList.add('active');
            
            // Efecto visual de border para el seleccionado
            swatches.forEach(s => s.style.border = '2px solid transparent');
            e.target.style.border = '2px solid var(--gray-700)';

            aplicarPreferencias(currentPrefs);
        });
    });

    // Guardar en BD
    btnSave.addEventListener('click', async () => {
        try {
            const userId = getActiveUserId();
            const response = await fetch(`${API_URL}/usuarios/${userId}/preferencias`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(currentPrefs)
            });

            if (!response.ok) throw new Error("Error al guardar preferencias");

            savedPrefs = { ...currentPrefs };
            cerrarModal();
            mostrarToast("Preferencias guardadas exitosamente");
        } catch (error) {
            console.error(error);
            mostrarToast("No se pudieron guardar las preferencias");
        }
    });

    // Restablecer por defecto
    btnReset.addEventListener('click', async () => {
        const defaultPrefs = {
            tema: 'claro',
            colorPrimario: '#1E88E5'
        };

        try {
            const userId = getActiveUserId();
            const response = await fetch(`${API_URL}/usuarios/${userId}/preferencias`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(defaultPrefs)
            });

            if (!response.ok) throw new Error("Error al restablecer preferencias");

            savedPrefs = { ...defaultPrefs };
            currentPrefs = { ...defaultPrefs };
            aplicarPreferencias(savedPrefs);
            actualizarUIModal();
            cerrarModal();
            mostrarToast("Configuración restablecida");
        } catch (error) {
            console.error(error);
            mostrarToast("Error al restablecer configuración");
        }
    });

    // --- Funciones Core ---

    async function cargarPreferenciasBD() {
        try {
            const userId = getActiveUserId();
            const response = await fetch(`${API_URL}/usuarios/${userId}/preferencias`);
            if (response.ok) {
                const prefs = await response.json();
                if (prefs.tema && prefs.colorPrimario) {
                    savedPrefs = prefs;
                    currentPrefs = { ...prefs };
                }
            }
        } catch (error) {
            console.error("Error al cargar preferencias", error);
        }
        
        // Aplicar al iniciar la app
        aplicarPreferencias(savedPrefs);
    }

    function aplicarPreferencias(prefs) {
        const root = document.documentElement;

        // Modo Oscuro / Claro
        if (prefs.tema === 'oscuro') {
            root.style.setProperty('--gray-50', '#121212');
            root.style.setProperty('--white', '#1E1E1E');
            root.style.setProperty('--gray-100', '#2C2C2C');
            root.style.setProperty('--gray-200', '#3C3C3C');
            root.style.setProperty('--gray-700', '#F1F3F5');
            root.style.setProperty('--gray-600', '#E9ECEF');
            root.style.setProperty('--gray-500', '#ADB5BD');
        } else {
            root.style.setProperty('--gray-50', '#FAFBFC');
            root.style.setProperty('--white', '#FFFFFF');
            root.style.setProperty('--gray-100', '#F1F3F5');
            root.style.setProperty('--gray-200', '#E9ECEF');
            root.style.setProperty('--gray-700', '#343A40');
            root.style.setProperty('--gray-600', '#495057');
            root.style.setProperty('--gray-500', '#868E96');
        }

        // Color Primario
        root.style.setProperty('--blue-600', prefs.colorPrimario);
        
        // Calcular colores derivados simples (idealmente con libreria, pero para este mock basta así)
        // Set blue-500 y blue-400 y header gradient
        root.style.setProperty('--blue-500', prefs.colorPrimario);
        root.style.setProperty('--blue-400', prefs.colorPrimario);
    }

    function actualizarUIModal() {
        temaSelect.value = currentPrefs.tema;
        
        swatches.forEach(swatch => {
            swatch.classList.remove('active');
            swatch.style.border = '2px solid transparent';
            if (swatch.getAttribute('data-color') === currentPrefs.colorPrimario) {
                swatch.classList.add('active');
                swatch.style.border = '2px solid var(--gray-700)';
            }
        });
    }

    function mostrarToast(mensaje) {
        const toast = document.getElementById('toast');
        const toastText = document.getElementById('toast-text');
        if (toast && toastText) {
            toastText.textContent = mensaje;
            toast.classList.add('show');
            setTimeout(() => {
                toast.classList.remove('show');
            }, 3000);
        }
    }
});
