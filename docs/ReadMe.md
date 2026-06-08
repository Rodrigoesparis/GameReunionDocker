# 🔐 AuthApp — Sistema de Autenticación de Usuarios

Aplicación full-stack de autenticación de usuarios con frontend en **Flutter** y backend en **Java (Spring Boot)**. Incluye registro, inicio de sesión, gestión de sesiones con JWT y panel de usuario.

---

## 📁 Estructura del Proyecto

```
proyecto/
├── frontend/                          # Aplicación Flutter
│   └── lib/
│       └── screens/
│           ├── Grupo/
│           │   ├── chat_overlay.dart
│           │   ├── create_group_screen.dart
│           │   ├── group_detail_screen.dart
│           │   ├── group_members_screen.dart
│           │   └── voice_channel_overlay.dart
│           ├── Iniciales/
│           │   ├── groups_screen.dart
│           │   ├── main_screen.dart
│           │   ├── ranking_screen.dart
│           │   ├── search_screen.dart
│           │   └── servers_screen.dart
│           └── Registro/
│               ├── login_screen.dart
│               ├── profile_screen.dart
│               ├── register_screen.dart
│               └── verify_screen.dart
├── backend/                           # API REST en Java (Spring Boot)
│   └── src/main/java/com/rodrigo/
│       ├── controlador/
│       │   ├── UserController.java
│       │   └── VoiceSignalingController.java
│       ├── modelo/
│       │   ├── GameReunion.java
│       │   ├── Games.java
│       │   ├── KarmaVote.java
│       │   ├── Lenguage.java
│       │   ├── Message.java
│       │   ├── Participant.java
│       │   ├── ParticipantId.java
│       │   ├── Platform.java
│       │   ├── Privacy.java
│       │   ├── Request.java
│       │   ├── RequestStatus.java
│       │   ├── Role.java
│       │   └── User.java
│       ├── repositorio/
│       │   ├── GameReunionRepository.java
│       │   ├── GamesRepository.java
│       │   ├── KarmaVoteRepository.java
│       │   ├── LenguageRepository.java
│       │   ├── MessageRepository.java
│       │   ├── ParticipantRepository.java
│       │   ├── PlatformRepository.java
│       │   ├── RequestRepository.java
│       │   └── UserRepository.java
│       ├── servicio/
│       │   ├── EmailService.java
│       │   ├── GameReunionService.java
│       │   ├── KarmaService.java
│       │   ├── ParticipantService.java
│       │   ├── ProfileService.java
│       │   └── UserService.java
│       └── GameReunionApplication.java
├── Scripts/                           # Scripts SQL de datos de ejemplo
│   ├── defaultdb_game_groups.sql
│   ├── defaultdb_karma_votes.sql
│   ├── defaultdb_messages.sql
│   ├── defaultdb_participants.sql
│   ├── defaultdb_requests.sql
│   ├── defaultdb_servidor.sql
│   ├── defaultdb_user_games.sql
│   ├── defaultdb_user_languages.sql
│   ├── defaultdb_user_platforms.sql
│   └── defaultdb_users.sql
├── .env                               # Variables de entorno (NO subir al repositorio)
├── .env.example                       # Plantilla de variables de entorno
└── Docs/
    ├── ReadMe.md
    └── Memoria_RodrigoEsparis/
```

---

## 🚀 Requisitos Previos

### Frontend
- [Flutter SDK](https://docs.flutter.dev/get-started/install) `>= 3.0.0`
- Dart `>= 3.0.0`
- Android Studio / VS Code con extensión Flutter
- Emulador Android/iOS o dispositivo físico

### Backend
- [Java JDK](https://adoptium.net/) `>= 17`
- [Maven](https://maven.apache.org/) `>= 3.8`
- Acceso a la base de datos en la nube (credenciales en `.env`)

---

## ⚙️ Configuración

### 1. Variables de entorno

Copia el archivo de plantilla y rellena tus credenciales:

```bash
cp .env.example .env
```

Edita el archivo `.env` con los datos de tu base de datos en la nube:


> ⚠️ **Importante:** El archivo `.env` está en `.gitignore` y **nunca debe subirse al repositorio**. Comparte las credenciales de forma segura con el equipo.

### 2. Backend — `application.properties`

El archivo ya está configurado para leer las variables de entorno:

```properties
# Base de datos
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.format_sql=true

# JWT
app.jwt.secret=${JWT_SECRET}
app.jwt.expiration-ms=${JWT_EXPIRATION_MS}

# Puerto del servidor
server.port=${SERVER_PORT}
```

### 3. Frontend — URL del backend

Edita el archivo `frontend/lib/services/api_service.dart` y ajusta la URL base:

```dart
const String baseUrl = 'http://localhost:8080/api';
```

> ℹ️ En emulador Android usa `http://10.0.2.2:8080/api` en lugar de `localhost`.

---

## 📦 Carga de Datos de Ejemplo

Los scripts SQL se encuentran en la carpeta `Scripts/`. Deben ejecutarse **en orden** tras arrancar el backend por primera vez (Hibernate habrá creado las tablas automáticamente).

### Orden de ejecución recomendado

```bash
# 1. Conecta a tu base de datos en la nube
mysql -h TU_HOST_EN_LA_NUBE -u TU_USUARIO -p defaultdb

# 2. Ejecuta los scripts en este orden (respeta dependencias entre tablas)
source Scripts/defaultdb_users.sql
source Scripts/defaultdb_servidor.sql
source Scripts/defaultdb_user_games.sql
source Scripts/defaultdb_user_languages.sql
source Scripts/defaultdb_user_platforms.sql
source Scripts/defaultdb_game_groups.sql
source Scripts/defaultdb_participants.sql
source Scripts/defaultdb_messages.sql
source Scripts/defaultdb_requests.sql
source Scripts/defaultdb_karma_votes.sql
```

### Con MySQL Workbench

1. Conéctate a tu base de datos en la nube desde MySQL Workbench.
2. Ve a **File → Open SQL Script** y selecciona cada archivo de `Scripts/`.
3. Ejecútalos en el orden indicado arriba con el botón ▶ **Execute**.

> 🔒 Las contraseñas de los usuarios de prueba están hasheadas con BCrypt en los scripts.

---

## ▶️ Instrucciones de Ejecución

### Backend (Java / Spring Boot)

```bash
# 1. Entra al directorio del backend
cd backend

# 2. Compila el proyecto
mvn clean install

# 3. Inicia el servidor
mvn spring-boot:run
```

El servidor estará disponible en: `http://localhost:8080`

> ℹ️ Al arrancar por primera vez, Hibernate generará automáticamente todas las tablas en la base de datos en la nube.

---


---

## 🛠️ Tecnologías Utilizadas

| Capa        | Tecnología                            |
|-------------|---------------------------------------|
| Frontend    | Flutter, Dart, Provider               |
| Backend     | Java 17, Spring Boot, Spring Security |
| ORM         | Hibernate (JPA)                       |
| Base datos  | MySQL 8 (nube)                        |
| Auth        | JWT (JSON Web Tokens), BCrypt         |
| Config      | Variables de entorno (.env)           |
| Build       | Maven, Flutter CLI                    |

---