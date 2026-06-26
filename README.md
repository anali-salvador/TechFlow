# TechFlow - Gestión de Inventario de Productos Tecnológicos

## Equipo
- **Anali Salvador Advíncula**
- **Ederd Carrasco Oscco**

## Temática
Aplicación móvil Android para la gestión de inventario de productos tecnológicos (laptops, celulares, accesorios, componentes), dirigida a técnicos independientes y pequeños negocios del rubro tecnológico.

## Tecnologías utilizadas
- Kotlin
- Jetpack Compose + Material Design 3
- Arquitectura MVVM
- Room (persistencia local)
- Retrofit + Gson (consumo de API REST)
- Hilt (inyección de dependencias)
- Navigation Compose
- Coroutines + StateFlow
- Firebase Authentication, Cloud Firestore y FCM (Parte 2)

## Arquitectura MVVM
La aplicación implementa el patrón Model-View-ViewModel separando las responsabilidades en capas. Las vistas (Composables en Jetpack Compose) solo muestran la interfaz y capturan eventos del usuario, sin contener lógica de negocio. Cada pantalla principal tiene su propio ViewModel que expone el estado de la UI mediante StateFlow y un data class UiState, y las vistas lo observan con collectAsState() para que la recomposición sea automática. El Repository es la única fuente de datos de los ViewModels, coordinando el acceso a Room para la persistencia local y a Retrofit para el consumo de la API externa.

## Estructura de paquetes
com.techflow.app/
├── ui/              → Pantallas y componentes (Composables)
├── viewmodel/       → ViewModels y UiStates
├── data/
│   ├── local/       → Room (Entity, DAO, Database)
│   ├── remote/      → Retrofit (ApiService, Response)
│   └── repository/  → ProductRepository
├── domain/model/    → Product, ProductMapper
├── di/              → Módulos Hilt
├── navigation/      → NavGraph, AppScreens
└── notifications/   → (Parte 2)

## Pantallas

### Pantalla 1 — Lista de Inventario (Pantalla Principal)
Muestra todos los productos del usuario en un LazyColumn. Cada producto muestra nombre, categoría, marca, precio y un indicador visual de stock (rojo si está bajo, verde si es normal). Tiene botón flotante para agregar productos y acceso a Explorar y Estadísticas.

### Pantalla 2 — Detalle del Producto
Muestra todos los campos del producto: nombre, categoría, marca, precio, cantidad, stock mínimo y descripción. Incluye botón de editar y eliminar con diálogo de confirmación. Muestra alerta visual cuando el stock está por debajo del mínimo.

### Pantalla 3 — Formulario Agregar / Editar
Formulario con campos para nombre, categoría (dropdown), marca, precio, cantidad, stock mínimo y descripción. Valida campos obligatorios antes de guardar. En modo edición los campos se precargan con los datos actuales del producto.

### Pantalla 4 — Explorar / Búsqueda por API
Permite buscar productos tecnológicos desde la API pública FakeStoreAPI mediante Retrofit. Muestra estado de carga con indicador circular y mensaje de error si falla la conexión. Cada resultado tiene un botón para agregar el producto al inventario local.

### Pantalla 5 — Estadísticas
Muestra tres tarjetas: total de productos registrados, cantidad de productos con stock bajo y valor total estimado del inventario (suma de precio × cantidad). Debajo lista los productos en riesgo de stock con acceso rápido a su detalle.

## Flujo MVVM
Usuario toca botón → Vista llama función del ViewModel → ViewModel llama al Repository → Repository accede a Room o API → Room/API devuelve datos → Repository los pasa al ViewModel → ViewModel actualiza el StateFlow → Vista hace recomposición automática

## API utilizada
- **FakeStoreAPI** — https://fakestoreapi.com/
- Endpoint: /products/category/electronics
- API pública gratuita, sin necesidad de API key

## Modelo de datos
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Int | PrimaryKey, autoGenerate |
| firestoreId | String | ID del documento en Firestore |
| nombre | String | Nombre del producto |
| categoria | String | Laptop, Celular, Accesorio, etc. |
| marca | String | Marca del producto |
| precio | Double | Precio unitario en soles |
| cantidad | Int | Cantidad actual en stock |
| stockMinimo | Int | Nivel mínimo antes de alerta |
| descripcion | String? | Descripción adicional (nullable) |
| userId | String | UID del usuario propietario |
| fechaRegistro | Long | Timestamp de creación |

