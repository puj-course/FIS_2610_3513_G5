const HorarioDataSubject = {
    observers: [],
    subscribe: (fn) => HorarioDataSubject.observers.push(fn),
    notify: (data) => HorarioDataSubject.observers.forEach(fn => fn(data))
};

const GridRenderer = {
    update: (data) => {
        data.forEach(item => {
            console.log("Dibujando bloque para: " + item.nombreAsignatura);
        });
    }
};

HorarioDataSubject.subscribe(GridRenderer.update);

