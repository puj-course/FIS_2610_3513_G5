/**
 * Dashboard Logic for StudyHub (HU-374)
 */

const API_BASE_URL = window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1' 
                    ? 'https://studyhub-c2ft.onrender.com/api' 
                    : window.location.origin + '/api';

document.addEventListener('DOMContentLoaded', () => {
    // Initial fetch of statistics
    // For now, using user ID 1 as placeholder
    fetchGlobalStatistics(1);
});

async function fetchGlobalStatistics(userId) {
    try {
        const response = await fetch(`${API_BASE_URL}/usuarios/${userId}/estadisticas`);
        if (!response.ok) throw new Error('Error al obtener estadísticas');
        
        const data = await response.json();
        updateStatsUI(data);
    } catch (error) {
        console.error('Fetch error:', error);
        // Handle empty state or error UI
        showEmptyState();
    }
}

function updateStatsUI(data) {
    // Update metric cards
    document.getElementById('stat-avg').textContent = data.promedioGlobal.toFixed(1);
    document.getElementById('stat-subjects').textContent = data.totalMaterias;
    document.getElementById('stat-credits').textContent = data.totalCreditos;
    document.getElementById('stat-risk').textContent = data.materiasEnRiesgo;

    // Update risk alerts
    const riskContainer = document.getElementById('risk-alerts');
    const riskList = document.getElementById('risk-list');
    riskList.innerHTML = '';

    const subjectsAtRisk = Object.entries(data.promediosPorMateria)
        .filter(([_, avg]) => avg < 3.0 && avg > 0);

    if (subjectsAtRisk.length > 0) {
        riskContainer.style.display = 'block';
        subjectsAtRisk.forEach(([name, avg]) => {
            const item = document.createElement('div');
            item.className = 'risk-item';
            item.innerHTML = `<span>${name}</span> <strong>${avg.toFixed(1)}</strong>`;
            riskList.appendChild(item);
        });
    } else {
        riskContainer.style.display = 'none';
    }

    // Render Chart
    renderAverageChart(data.promediosPorMateria);
}

let averageChart = null;
function renderAverageChart(promedios) {
    const ctx = document.getElementById('averageChart').getContext('2d');
    
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
                label: 'Promedio por Materia',
                data: values,
                backgroundColor: values.map(v => v < 3.0 ? 'rgba(229, 57, 53, 0.6)' : 'rgba(30, 136, 229, 0.6)'),
                borderColor: values.map(v => v < 3.0 ? 'rgba(229, 57, 53, 1)' : 'rgba(30, 136, 229, 1)'),
                borderWidth: 1,
                borderRadius: 5
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: {
                    beginAtZero: true,
                    max: 5,
                    ticks: {
                        stepSize: 1
                    },
                    grid: {
                        display: true,
                        color: 'rgba(0,0,0,0.05)'
                    }
                },
                x: {
                    grid: {
                        display: false
                    }
                }
            },
            plugins: {
                legend: {
                    display: false
                }
            }
        }
    });
}

function showEmptyState() {
    // Optional: Reset UI to neutral values if fetch fails
    document.getElementById('stat-avg').textContent = '-';
    document.getElementById('stat-subjects').textContent = '0';
    document.getElementById('stat-credits').textContent = '0';
    document.getElementById('stat-risk').textContent = '0';
}
