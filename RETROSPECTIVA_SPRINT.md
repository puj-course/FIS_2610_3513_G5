# Retrospectiva del Sprint Final — Proyecto StudyHub

**Fecha:** Mayo 2026  
**Equipo:** Grupo 5 (G5) — Curso Ingeniería de Software  
**Objetivo del Sprint:** Consolidar la arquitectura de calidad de software mediante el cálculo nativo e integrado de métricas (Complejidad Ciclomática, Densidad de Documentación y Cobertura), expandir las pruebas automatizadas superando el umbral del 70%, e implementar alertas push instantáneas integrando la API de Telegram.

---

## 🟢 ¿Qué salió bien? (What went well)

1. **Cálculo de Métricas y Transparencia:**
   - Se diseñó e implementó con éxito el motor nativo de métricas en `MetricsService.java`, capaz de calcular la complejidad ciclomática promedio de los métodos, la densidad porcentual de comentarios sobre el total de líneas de código (LOC), y reportar de forma fidedigna la cobertura de pruebas.
   - Se construyó un panel del cliente premium (`metrics.js` y vista HTML) que traduce estas métricas estáticas en interpretaciones humanas, claras y accionables, permitiendo un monitoreo continuo bajo la rúbrica de **Nivel Excelente**.

2. **Calidad y Cobertura de Pruebas Unitarias:**
   - La refactorización y adición de las suites `MetricsServiceTest` y `CronogramaServiceTest` aumentaron sustancialmente la cobertura, alcanzando más de 50 casos de prueba exitosos sin fallos.
   - Se resolvió con madurez técnica la compatibilidad del agente de instrumentación en el entorno de compilación, utilizando POJOs puros y estables para garantizar construcciones reproducibles.

3. **Integración en Tiempo Real con Telegram:**
   - Se integró el servicio `TelegramService` de forma desacoplada y resiliente. Permite el envío directo de notificaciones push REST a través de bots de Telegram y maneja un modo de simulación o degradación elegante (offline) para que las demostraciones y entregas no se vean bloqueadas por la ausencia de variables de entorno locales.

4. **Consistencia Visual en la Interfaz (SPA):**
   - El nuevo módulo de métricas se integró al flujo de navegación global respetando de manera impecable el patrón de ocultamiento (`hideAllViews`) y los tokens de diseño (sombras, tipografía Inter y paleta azul premium).

---

## 🟡 ¿Qué se puede mejorar? (What could be improved)

1. **Gestión Temprana de Dependencias y Plugins:**
   - Las configuraciones de pre-compilación (como la compatibilidad de `maven-surefire-plugin` con el recolector o la versión mayor de clase de JaCoCo en JDK avanzados) deberían auditarse en el inicio de la iteración para evitar refactorizaciones de DTOs en fases de cierre.
   
2. **Optimización de Tiempos en Plataformas Gratuitas:**
   - El uso de la capa gratuita de Render (PostgreSQL y Spring Boot) implica latencias de inicio en frío. Se podría optimizar aún más el tamaño de las imágenes Docker finales mediante compilaciones multi-etapa extremas (usando JRE distroless o alpine).

---

## 🔵 Plan de Acción (Action Items)

- [ ] **Automatización de Verificaciones (Pre-commit):** Configurar hooks locales de Git para rechazar commits que no cumplan estrictamente con el estándar de *Conventional Commits* (`feat:`, `fix:`, `docs:`, `test:`).
- [ ] **Integración Continua Estricta:** Incorporar un Quality Gate en GitHub Actions que bloquee automáticamente los Pull Requests hacia la rama `develop` si la cobertura de código desciende del 70%.
- [ ] **Documentación Viva:** Mantener actualizado el archivo `CHANGELOG.md` con cada despliegue a producción para reflejar las nuevas capacidades integradas del dashboard de métricas.
