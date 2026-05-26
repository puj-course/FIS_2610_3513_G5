/**
 * Dashboard Enhancements for StudyHub
 * This script extends Valeria's base dashboard logic with charts and risk alerts.
 */

(function() {
    // Save reference to original function
    const originalRender = window.renderDashboardSummary;
    
    // Override the global function
    window.renderDashboardSummary = async function() {
        // 1. Call original to populate basic cards and task list
        if (typeof originalRender === 'function') {
            await originalRender();
        }
        
        // 2. Fetch enhanced statistics
        const userId = typeof getUserId === 'function' ? getUserId() : null;
        if (!userId) return;

        try {
            // API constant is global from index.html
            const response = await fetch(`${API}/api/usuarios/${userId}/estadisticas`);
            if (response.ok) {
                const data = await response.json();
                
                // Update Valeria's IDs with precise data from backend
                const avgElem = document.getElementById('dash-avg');
                const countElem = document.getElementById('dash-subjects-count');
                
                if (avgElem) avgElem.textContent = data.promedioGlobal.toFixed(1);
                if (countElem) countElem.textContent = data.totalMaterias;
                
                // 3. Render Risk Alerts (HU-377):
                //    Combina los promedios calculados desde BD con las materias
                //    detectadas en tiempo real por EstadisticasObserver.
                await renderRiskAlerts(data.promediosPorMateria, userId);
                
                // 4. Render Performance Chart (HU-374)
                renderAverageChart(data.promediosPorMateria);
            }
        } catch (error) {
            console.error("Dashboard enhancement error:", error);
        }
    };

    /**
     * Renderiza la sección "Materias que requieren atención".
     * Combina dos fuentes:
     *  - promediosPorMateria: mapa completo calculado desde BD (puede incluir materias >= 3)
     *  - /notas/materias-en-riesgo: materias que cruzaron el umbral en esta sesión (Observer)
     */
    async function renderRiskAlerts(promediosPorMateria, userId) {
        const riskContainer = document.getElementById('risk-alerts');
        const riskList = document.getElementById('risk-list');
        if (!riskContainer || !riskList) return;

        // Materias con promedio < 3 según BD
        const subjectsAtRisk = Object.entries(promediosPorMateria)
            .filter(([_, avg]) => avg < 3.0 && avg > 0);

        // Materias detectadas en tiempo real por EstadisticasObserver
        let observerRisk = new Set();
        try {
            const res = await fetch(`${API}/notas/materias-en-riesgo/${userId}`);
            if (res.ok) {
                const json = await res.json();
                observerRisk = new Set(json.materiasEnRiesgo || []);
            }
        } catch (e) {
            console.warn("No se pudo consultar materias-en-riesgo del observer:", e);
        }

        // Unir ambas fuentes: BD + observer (evitar duplicados por nombre)
        const combinado = new Map(subjectsAtRisk);
        for (const nombre of observerRisk) {
            if (!combinado.has(nombre)) {
                // Materia detectada por observer pero aún sin promedio en BD
                // (puede ocurrir si la nota recién guardada aún no llegó a estadísticas)
                combinado.set(nombre, promediosPorMateria[nombre] ?? null);
            }
        }

        riskList.innerHTML = '';

        if (combinado.size > 0) {
            riskContainer.style.display = 'block';
            for (const [name, avg] of combinado.entries()) {
                const item = document.createElement('div');
                item.style = "display: flex; justify-content: space-between; background: #fff; padding: 10px 16px; border-radius: 8px; border-left: 4px solid #E65100; box-shadow: 0 1px 2px rgba(0,0,0,0.05);";
                item.innerHTML = `
                    <span style="font-weight: 500; color: #37474F;">${name}</span>
                    <strong style="color: #d32f2f;">${avg != null ? avg.toFixed(1) : '—'}</strong>
                `;
                riskList.appendChild(item);
            }
        } else {
            riskContainer.style.display = 'none';
        }
    }

    let averageChart = null;
    function renderAverageChart(promedios) {
        const canvas = document.getElementById('averageChart');
        if (!canvas) return;

        const ctx = canvas.getContext('2d');
        if (averageChart) {
            averageChart.destroy();
        }

        const labels = Object.keys(promedios);
        const values = Object.values(promedios);

        averageChart = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Promedio',
                    data: values,
                    backgroundColor: values.map(v => v < 3.0 ? 'rgba(229, 57, 53, 0.6)' : 'rgba(30, 136, 229, 0.6)'),
                    borderColor: values.map(v => v < 3.0 ? 'rgba(229, 57, 53, 1)' : 'rgba(30, 136, 229, 1)'),
                    borderWidth: 1,
                    borderRadius: 6
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true,
                        max: 5,
                        grid: { color: 'rgba(0,0,0,0.05)' }
                    },
                    x: {
                        grid: { display: false }
                    }
                },
                plugins: {
                    legend: { display: false }
                }
            }
        });
    }
})();