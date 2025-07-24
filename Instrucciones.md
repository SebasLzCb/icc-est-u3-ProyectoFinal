# Proyecto Final – Estructura de Datos: Resolución de Laberintos con Búsqueda y Optimización

Este proyecto implementa distintas estrategias algorítmicas para la resolución de laberintos, aplicando estructuras de datos, patrones de diseño y técnicas de programación orientada a objetos. El enfoque principal es comparar diversos algoritmos de búsqueda en un entorno visual basado en Java y Swing.


---

## Estructura del Proyecto (MVC + DAO)

```
src/
├── MazeApp.java                  # Punto de entrada principal
├── controllers/                 # Controlador general de interacción entre vista y modelo
│   └── MazeController.java
├── dao/                        # Interfaces de acceso a datos
│   ├── AlgorithmResultDAO.java
│   └── daoImpl/                # Implementaciones concretas de DAO
│       └── AlgorithmResultDAOFile.java
├── models/                     # Clases de datos y modelos lógicos
│   ├── AlgorithmResult.java
│   ├── Cell.java
│   ├── CellState.java
│   └── SolveResults.java
├── resources/                  # (Reservado para futuros assets)
├── solver/                     # Interfaces de algoritmos de resolución
│   ├── MazeSolver.java
│   └── solverImpl/             # Implementaciones de algoritmos
│       ├── MazeSolverBFS.java
│       ├── MazeSolverDFS.java
│       ├── MazeSolverRecursivo.java
│       ├── MazeSolverRecursivoCompleto.java
│       └── MazeSolverRecursivoCompletoBT.java
└── views/                      # Interfaz gráfica
    ├── MazeFrame.java
    ├── MazePanel.java
    └── ResultadosDialog.java   # Tabla y gráfica de resultados
```

---

## Descripción del Problema

Se busca resolver un laberinto representado como una matriz de celdas transitables o no transitables, buscando encontrar la ruta más corta entre un punto de inicio A y uno de destino B.

---

## Algoritmos Implementados

Cada algoritmo está implementado en una clase diferente dentro de `solverImpl/`:

* Recursivo (2 direcciones)
* Recursivo (4 direcciones)
* Recursivo (4 direcciones + Backtracking)
* BFS (Breadth-First Search)
* DFS (Depth-First Search)

---

## Interfaz de Usuario (Java Swing)

* Permite seleccionar el tamaño del laberinto.
* Permite marcar celdas como inicio, fin o muro.
* Ejecuta el algoritmo seleccionado y muestra visualmente el camino encontrado.
* Visualiza resultados anteriores en una **tabla**.
* Opcion de graficar tiempos de ejecución por algoritmo usando **JFreeChart**.

---

## Resultados y Archivo de Registro

Los resultados se guardan en un archivo `results.csv`, utilizando un DAO (`AlgorithmResultDAO`) para mantener independencia de la lógica de persistencia.

Desde la interfaz:

* Se puede ver una tabla con: nombre del algoritmo, longitud del camino, tiempo de ejecución.
* Se puede graficar los tiempos por algoritmo.
* Se puede limpiar el archivo.
* No se permite eliminar registros individuales.
* No se permite registros duplicados. Si se intenta agregar un registro con el mismo nombre de algoritmo, se actualiza el tiempo de ejecución y el numero de celdas.

---

## Programación Dinámica y Optimizations

* En futuras versiones se podría incorporar memoización o tabulación para reducir la complejidad temporal en algunos algoritmos recursivos.

---

## UML (pendiente agregar imagen)

Aquí se incluirá el diagrama UML con relación entre clases:

* Modelo-Vista-Controlador (MVC)
* DAO con archivo CSV
* Estrategia de algoritmos (`MazeSolver` y sus implementaciones)

---

## Requisitos para el Informe (README personal del estudiante)

Este archivo `README.md` será reemplazado por tu versión final, la cual debe contener:

1. **Carátula**: logo de la universidad, materia, nombre y correo institucional.
2. **Descripción del problema.**
3. **Propuesta de solución**:

   * Marco teórico (DFS, BFS, backtracking, recursión).
   * Tecnologías utilizadas.
   * Diagrama UML con su explicación.
   * Capturas de la interfaz. 2 ejemplos en diferentes laberintos. escojer un algoritmo y mostrar su funcionamiento.
   * Codigo ejemplo de un algoritmo (puede ser el que tú quieras, pero debe ser uno de los implementados). Codigo comentado y explicado.


4. **Conclusiones**: cuál algoritmo es más óptimo y por qué. (POR ESTUDIANTE)
5. **Recomendaciones y aplicaciones futuras.**

---

## Instrucciones Finales para Entrega

1. Ejecuta y prueba el proyecto con varios casos.
2. Completa el archivo `README.md` con tu informe.
3. Realiza commit con el mensaje:

   ```
   Proyecto Final – Estructura de Datos
   ```
4. Genera el Jar ejecutable del proyecto.
   * Asegúrate de que el JAR incluya todas las dependencias necesarias.
5. Sube el repositorio a GitHub. CODIGO PÚBLICO + README (IMFORME) + JAR.
6. Entrega la URL del repositorio en el AVAC.

---

## Contribuciones

* Pablo Torres – Docente
