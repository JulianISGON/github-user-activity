# GitHub User Activity

Una aplicación Spring Boot simple que consulta la API de GitHub para mostrar la actividad reciente de un usuario específico. Muestra eventos como pushes, creaciones de repositorios, ramas, issues, stars y forks.

## Características

- **Consulta de Actividad**: Obtiene los últimos 10 eventos públicos de un usuario de GitHub.
- **Formatos Detallados**: Cada tipo de evento se formatea de manera clara y específica (ej. "Created branch main in usuario/repo").
- **Manejo de Errores**: Gestiona errores comunes como usuario no encontrado o límite de tasa de la API.
- **Sin Dependencias Externas Adicionales**: Usa solo bibliotecas estándar de Spring Boot y Jackson para JSON.

## Requisitos Previos

- **Java**: Versión 17 o superior.
- **Gradle**: Versión 7.0 o superior (viene incluido con el wrapper).
- **Conexión a Internet**: Para acceder a la API de GitHub.

## Instalación

1. Clona el repositorio:
   ```bash
   git clone https://github.com/JulianISGON/github-user-activity.git
   cd github-user-activity
   ```

2. Construye el proyecto:
   ```bash
   ./gradlew build
   ```

## Uso

Ejecuta la aplicación pasando el nombre de usuario de GitHub como argumento:

```bash
./gradlew bootRun --args='nombre_de_usuario'
```

### Ejemplo

```bash
./gradlew bootRun --args='octocat'
```

Salida de ejemplo:
```
Started GitHubUserActivityApplication in 0.721 seconds
Pushed 3 commits to octocat/Hello-World
Created branch main in octocat/Spoon-Knife
Starred octocat/linguist
Opened a new issue in octocat/Hello-World
```

### Configuración en IntelliJ IDEA

Para configurar un runner en IntelliJ IDEA:

- **Gradle project**: Ruta al directorio raíz del proyecto (ej. `C:\Users\julia\OneDrive\Desktop\julian\Repositorios\GitHub User Activity`).
- **Run**: `bootRun --args='tu_usuario'`.
- **Environment variables**: Deja vacío.

Guarda la configuración y ejecuta.

## Estructura del Proyecto

```
src/
├── main/
│   ├── java/io/github/julianisaza/
│   │   └── GitHubUserActivityApplication.java  # Clase principal
│   └── resources/
│       └── application.properties              # Configuración (vacío por defecto)
└── test/
    └── java/io/github/julianisaza/
        └── GitHubUserActivityApplicationTests.java  # Pruebas unitarias
build.gradle  # Dependencias y configuración de Gradle
```

## API de GitHub

La aplicación utiliza la API pública de GitHub (`https://api.github.com/users/{username}/events`). No requiere autenticación para eventos públicos, pero está sujeta a límites de tasa (60 requests por hora para IPs no autenticadas).

### Eventos Soportados

- **PushEvent**: Muestra el número de commits empujados.
- **CreateEvent**: Detalla si se creó un repositorio, rama o etiqueta.
- **IssuesEvent**: Indica si se abrió o cerró un issue.
- **WatchEvent**: Muestra cuando se estrella un repositorio.
- **ForkEvent**: Muestra cuando se hace fork de un repositorio.

Otros eventos se omiten silenciosamente.

## Contribución

¡Las contribuciones son bienvenidas! Para contribuir:

1. Haz un fork del repositorio.
2. Crea una rama para tu feature: `git checkout -b feature/nueva-funcionalidad`.
3. Realiza tus cambios y agrega pruebas.
4. Ejecuta las pruebas: `./gradlew test`.
5. Envía un pull request.

### Mejoras Sugeridas

- Agregar soporte para más tipos de eventos (ej. PullRequestEvent, ReleaseEvent).
- Implementar autenticación con token de GitHub para aumentar el límite de tasa.
- Agregar opciones de filtrado (ej. por tipo de evento o fecha).
- Crear una interfaz web simple para la aplicación.

## Licencia

Este proyecto está bajo la Licencia MIT. Consulta el archivo `LICENSE` para más detalles.

## Autor

**Julian Isaza**  
- GitHub: [julianisaza](https://github.com/julianisaza)  
- Email: julian.isaza1020@gmail.com
- URL: https://github.com/JulianISGON/github-user-activity
---

