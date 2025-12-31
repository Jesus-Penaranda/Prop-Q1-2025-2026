# Extractor de Prototipos de Comportamiento (Clustering System)

<div align="center">

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Swing](https://img.shields.io/badge/GUI-Java%20Swing-orange?style=for-the-badge)
![FIB](https://img.shields.io/badge/FIB-DC2626?style=for-the-badge)
![UPC](https://img.shields.io/badge/UPC-0070BB?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-Finalizado-success?style=for-the-badge)

**Proyecto de Diseño y Programación (PROP) - FIB UPC (Curso 25/26)**

> Sistema avanzado de análisis de datos diseñado para identificar automáticamente perfiles de comportamiento utilizando algoritmos de Clustering, Modelo de Espacio Vectorial y simulación gráfica.

</div>

---

## Descripción del Proyecto

Este software implementa una solución completa para la **Gestión y Análisis de Encuestas**. Utiliza técnicas de aprendizaje no supervisado para agrupar individuos basándose en la similitud de sus respuestas, permitiendo identificar patrones de comportamiento (perfiles) en conjuntos de datos heterogéneos.

### Funcionalidades Principales

* **Gestión Integral:** Creación interactiva de encuestas, importación masiva (CSV), edición dinámica y eliminación.
* **Análisis de Clustering:** Ejecución de algoritmos para segmentar usuarios en $k$ grupos.
* **Visualización 2D (Extra):** Simulación gráfica que proyecta los resultados multidimensionales en un plano 2D para visualizar la distribución de los clústers en tiempo real.
* **Persistencia y Sesiones (Extra):** Sistema de Login multi-usuario con guardado automático del estado de trabajo.
* **Filtrado Selectivo (Extra):** Capacidad para ignorar preguntas específicas durante el análisis para reducir el ruido.

---

## Detalles Técnicos y Algoritmos

El núcleo del sistema se basa en el **Modelo de Espacio Vectorial**, donde cada encuestado se representa como un vector $X_i$ y la similitud se calcula mediante distancias normalizadas.

### Algoritmos de Clustering

El sistema implementa el patrón de diseño **Strategy** para intercambiar algoritmos y métodos de inicialización dinámicamente:

| Algoritmo | Descripción Técnica | Inicialización |
| :--- | :--- | :--- |
| **K-Means** | Minimización de la varianza intra-clúster iterativa. Calcula centroides mediante medias (numéricas) y modas (categóricas). | Aleatoria (Trivial) o K-Means++. |
| **K-Medoids** | *(Extra)* Utiliza individuos reales del dataset como centros (PAM). Más robusto frente a outliers y datos mixtos. | Selección Trivial. |
| **K-Means++** | Inicialización probabilística ($D^2$) para optimizar la convergencia inicial y evitar mínimos locales. | Estocástica. |

### Métricas de Distancia (Normalizadas a $[0, 1]$)

Para garantizar la equidad entre atributos de distinta naturaleza, el sistema normaliza todas las distancias locales al intervalo $[0, 1]$. La distancia global entre dos individuos es la suma ponderada de estas métricas:

**1. Numéricas (Cuantitativas)**
Diferencia absoluta normalizada por el rango dinámico del atributo.

$$
D_{num}(x, y) = \frac{|x - y|}{V_{max} - V_{min}}
$$

**2. Cualitativas Ordenadas (Likert/Escalas)**
Distancia basada en el orden jerárquico ($rank$) de las modalidades.

$$
D_{ord}(x, y) = \frac{|rank(x) - rank(y)|}{N_{modalidades} - 1}
$$

**3. Cualitativas (Conjuntos/Respuesta Múltiple)**
Basada en la disimilitud de Jaccard, comparando la intersección y unión de las selecciones.

$$
D_{set}(A, B) = 1 - \frac{|A \cap B|}{|A \cup B|}
$$

**4. Texto Libre (Strings)**
Distancia de edición de Levenshtein normalizada por la longitud de la cadena más larga.

$$
D_{str}(s_1, s_2) = \frac{\text{Levenshtein}(s_1, s_2)}{\max(|s_1|, |s_2|)}
$$

**5. Cualitativa Simple (Nominal)**
Función binaria de coincidencia exacta.

$$
D_{nom}(x, y) =
\begin{cases}
0 & \text{si } x = y \\
1 & \text{si } x \neq y
\end{cases}
$$

### Optimización y Calidad

* **Coeficiente de Silhouette:** Evalúa la cohesión $a(i)$ y separación $b(i)$ de cada asignación. Un valor cercano a $1$ indica una agrupación ideal.

$$
s(i) = \frac{b(i) - a(i)}{\max\{a(i), b(i)\}}
$$

* **Selección Automática de $K$ :** El sistema realiza un análisis iterativo ejecutando el algoritmo para un rango de valores de $k$. En cada paso se calcula el promedio global de Silhouette y el sistema selecciona automáticamente el valor de $k$ que maximiza la calidad del agrupamiento.

---

## Arquitectura del Sistema

El proyecto sigue estrictamente una **Arquitectura de Tres Capas** para garantizar desacoplamiento y mantenibilidad:

1.  **Capa de Presentación (Vistas + CtrlPresentacion):**
    * Interfaz gráfica construida con Java Swing.
    * Uso de `CardLayout` para navegación fluida.
    * Validación de tipos de datos en tiempo real.

2.  **Capa de Dominio (Clases + CtrlDominio):**
    * Contiene toda la lógica matemática y los algoritmos.
    * Gestiona la sesión del usuario y el ciclo de vida de las encuestas.

3.  **Capa de Persistencia (Gestores + CtrlPersistencia):**
    * Almacenamiento en archivos `.csv` (Encuestas, Respuestas, Perfiles).
    * Separación de responsabilidades mediante gestores especializados (`GestorEncuestas`, `GestorPerfiles`, etc.).

---

## Estructura del Directorio

```text
├── DOCS/                   # Documentación técnica y diagramas
│   ├── DiagramaClasesCapaDominio.pdf
│   ├── DiagramaClasesCapaPersistencia.pdf
│   ├── DiagramaClasesCapaPresentacion.pdf
│   ├── DiagramaDeClases.pdf
│   ├── Diagrama_de_uso.pdf
│   └── Documentación_PROP.pdf
└── EXE/                    # Carpeta principal del Ejecutable y Fuentes
    ├── Datos/              # Base de datos local (Archivos persistentes)
    ├── src/                # Código fuente
    │   ├── main/java/
    │   │   ├── dominio/      # Lógica de negocio (Algoritmos, Clases Base)
    │   │   ├── Persistencia/ # Gestión de archivos (Gestores CSV)
    │   │   ├── Presentacion/ # Vistas (Swing) y Controladores de UI
    │   │   ├── drivers/      # Drivers de consola para pruebas manuales
    │   │   └── Main.java     # Punto de entrada de la aplicación
    │   └── test/java/Tests/  # Tests Unitarios e Integración
    ├── gradle/             # Configuración Gradle Wrapper
    ├── build.gradle        # Dependencias del proyecto
    ├── Ejecutar_script.sh  # Script de automatización
    ├── gradlew             # Ejecutable Gradle (Unix)
    ├── gradlew.bat         # Ejecutable Gradle (Windows)
    └── usuarios_encuestas.txt # Usuarios y contraseñas registradas
```
---

## Instrucciones de Instalación y Ejecución

Se incluye un script de automatización (`Ejecutar_script.sh`) en la raíz del proyecto.

### Requisitos Previos
* **Java JDK:** Versión 11 o superior.
* **Terminal:** Bash (Linux/macOS) o Git Bash (Windows).

### Pasos para ejecutar

1.  Otorga permisos de ejecución al script:
    ```bash
    chmod +x Ejecutar_script.sh
    ```
2.  Ejecuta el script para compilar y lanzar el menú:
    ```bash
    ./Ejecutar_script.sh
    ```

### Menú de Opciones
Al iniciar, verás las siguientes opciones (basado en el Manual de Ejecución):

* `[0]` **Ejecutar Interfaz Gráfica:** Lanza la aplicación completa (GUI). **(Recomendado)**.
* `[1]` **Driver AUTOMÁTICO-1:** Ejecuta `DriverEjemploCompleto` (Demo rápida de integración).
* `[2]` **Driver AUTOMÁTICO-2:** Ejecuta `DriverComparativa` (Benchmark K-Means vs K-Medoids).
* `[3]` **Driver INTERACTIVO:** Lanza `DriverVisual` (Consola manual).
* `[4]` **Ejecutar TESTS:** Ejecuta la suite de pruebas JUnit y genera el reporte HTML.
* `[5]` **Salir.**

---
