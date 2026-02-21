\# TAD: Horario Semanal (Estructura lógica)



\## Tipos auxiliares



\### Tipo `DiaSemana`

Conjunto finito:

`DiaSemana = {LUN, MAR, MIE, JUE, VIE, SAB, DOM}`



\### Tipo `Hora`

Representación en formato 12 horas:

`Hora = "HH:MM"`



---



\# TAD 1: Asignatura



\## Descripción

Representa una materia registrada con su información académica y su horario semanal.



\## Atributos

\- `Nombre: String`

\- `Profesor: String`

\- `Edificio: String`

\- `Salón: String`

\- `DíasSemana: Lista<DiaSemana>`

\- `HoraInicio: Hora`

\- `HoraFin: Hora`



\## Invariantes

1\. `Nombre`, `Profesor`, `Edificio`, `Salón` no son vacíos.

2\. `DíasSemana` no es vacío.

3\. `HoraInicio < HoraFin` (en tiempo real, no alfabético).

4\. El rango horario es válido en 12h (00:00–12:59).



\## Operaciones



\### Constructora

\- `CrearAsignatura(nombre, profesor, edificio, salón, días, hInicio, hFin) -> Asignatura`

&nbsp; - Pre: se cumplen invariantes (2,3,4).

&nbsp; - Post: retorna una asignatura con los atributos definidos.



\### Consultas

\- `ObtenerNombre(a) -> String`

\- `ObtenerProfesor(a) -> String`

\- `ObtenerUbicación(a) -> (Edificio, Salón)`

\- `ObtenerDías(a) -> Conjunto<DiaSemana>`

\- `ObtenerHorario(a) -> (HoraInicio, HoraFin)`



\### Actualizaciones

\- `CambiarProfesor(a, profesorNuevo) -> Asignatura`

\- `CambiarUbicación(a, edificioNuevo, salónNuevo) -> Asignatura`

\- `CambiarHorario(a, díasNuevo, hInicioNuevo, hFinNuevo) -> Asignatura`

&nbsp; - Pre: `díasNuevo` no vacío y `hInicioNuevo < hFinNuevo`.

&nbsp; - Post: se actualizan días/horas manteniendo invariantes.



---



\# TAD 2: HorarioSemanal



\## Atributos

\- `AsignaturasRegistradas: Conjunto<Asignatura>`



> Nota: La vista “por día” se obtiene filtrando las asignaturas cuyo `DíasSemana` incluye ese día y ordenándolas por `HoraInicio`. Esto permite “bloques por día y hora” sin duplicar datos.



\## Invariantes 

1\. No existen dos asignaturas que se crucen en el tiempo en un mismo día:

&nbsp;  - Para cualquier par `a1`, `a2` distintas:

&nbsp;    - Si `Intersección(ObtenerDías(a1), ObtenerDías(a2))` no es vacía, entonces sus rangos no se solapan:

&nbsp;      - `HoraFin(a1) <= HoraInicio(a2)` \*\*o\*\* `HoraFin(a2) <= HoraInicio(a1)`

2\. El horario soporta semana completa: se puede consultar cualquier `DiaSemana`.



\## Operaciones



\### Constructora

\- `CrearHorario() -> HorarioSemanal`

&nbsp; - Post: `AsignaturasRegistradas` inicia vacío.



\### Operaciones principales

\- `RegistrarAsignatura(h, a) -> HorarioSemanal`

&nbsp; - Pre:

&nbsp;   - `a` cumple invariantes de Asignatura.

&nbsp;   - `a` no genera choque con las ya registradas en `h` (ver `HayChoque`).

&nbsp; - Post:

&nbsp;   - `a` ∈ `AsignaturasRegistradas(h)`.



\- `EliminarAsignatura(h, nombre) -> HorarioSemanal`

&nbsp; - Pre: existe una asignatura con `Nombre = nombre`.

&nbsp; - Post: esa asignatura ya no está en `AsignaturasRegistradas(h)`.



\- `ActualizarAsignatura(h, nombre, aNueva) -> HorarioSemanal`

&nbsp; - Pre:

&nbsp;   - existe la asignatura `nombre`.

&nbsp;   - `aNueva` cumple invariantes.

&nbsp;   - reemplazarla no genera choques.

&nbsp; - Post:

&nbsp;   - se reemplaza la antigua por `aNueva`.



\### Consultas / vistas (bloques por día)

\- `ObtenerBloquesPorDia(h, d: DiaSemana) -> Lista<Asignatura>`

&nbsp; - Post:

&nbsp;   - retorna todas las asignaturas `a` tales que `d ∈ ObtenerDías(a)`,

&nbsp;   - ordenadas ascendentemente por `HoraInicio`.



\- `ObtenerSemana(h) -> Mapa<DiaSemana, Lista<Asignatura>>`

&nbsp; - Post:

&nbsp;   - para cada día `d` en `DiaSemana`, retorna `ObtenerBloquesPorDia(h,d)`.



\### Validación de choques

\- `HayChoque(h, aNueva) -> Boolean`

&nbsp; - Post:

&nbsp;   - retorna `true` si existe `a` en `AsignaturasRegistradas(h)` tal que

&nbsp;     comparten al menos un día y sus intervalos `\[HoraInicio, HoraFin)` se solapan.

&nbsp;   - retorna `false` en caso contrario.



---



\## Ejemplo de uso (conceptual)

1\. Se crea `h = CrearHorario()`.

2\. Se registra:

&nbsp;  - `a = CrearAsignatura("Fundamentos Ing. SW", "Profe X", "Edif A", "101", {LUN, MIE}, "07:00", "09:00")`

&nbsp;  - `h = RegistrarAsignatura(h, a)`

3\. Para visualizar el lunes:

&nbsp;  - `ObtenerBloquesPorDia(h, LUN)` devuelve la lista ordenada por hora.

