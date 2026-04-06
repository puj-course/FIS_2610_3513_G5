// Sujeto que notifica cambios en los datos del horario
const HorarioDataSubject = {
    observers: [],
    subscribe: (fn) => HorarioDataSubject.observers.push(fn),
    notify: (data) => HorarioDataSubject.observers.forEach(fn => fn(data))
};

// Observador concreto para renderizar bloques en el calendario
const GridRenderer = {
    update: (data) => {
        data.forEach(item => {
            console.log("Dibujando bloque para: " + item.nombreAsignatura);
            // Lógica para posicionar dinámicamente en el DOM
        });
    }
};

// Suscripción
HorarioDataSubject.subscribe(GridRenderer.update);