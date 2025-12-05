# Test Unitarios
El proyecto fue generado utilizando JHipster, lo cual incluye por defecto una amplia base de tests unitarios y de integración tanto en el frontend (Angular) como en el backend (Java/Spring Boot).

JHipster genera automáticamente:

Tests unitarios de componentes Angular
(*.component.spec.ts)

Tests de servicios Angular
(*.service.spec.ts)

Tests de pipes, directivas y resolvers
(*.spec.ts)

Tests backend de entidades, controladores REST, repositorios y utilidades
(src/test/java/...)

Esto provee una cobertura inicial muy completa del comportamiento estándar de la aplicación.

### ✔ Tests desarrollados para este trabajo

Además de los tests generados automáticamente, se implementaron dos tests propios, uno en el backend y otro en el frontend para asegurar la correcta lógica de negocio incorporada al sistema.

1) Test de unidad en Java
Archivo ''' WorkoutExerciseRelationshipTest.java '''

Este test valida la consistencia de la relación entre las entidades Workout y Exercise.
Se prueban los siguientes casos:

- Agregar un ejercicio a un entrenamiento.
- Evitar duplicados al agregar ejercicios.
- Eliminar un ejercicio de un entrenamiento.

Con esto se verifica que la lógica de asociación en el dominio funciona correctamente y mantiene integridad en la estructura de datos.

2) Test de unidad en Angular
Archivo ''' workout-update.component.spec.ts '''

Este test evalúa la lógica de selección de ejercicios en el formulario de edición de un entrenamiento.
Los casos probados incluyen:

- Detectar si un ejercicio está marcado como seleccionado.
- Agregar un ejercicio al listado cuando el usuario lo activa.
- Quitar un ejercicio cuando el usuario lo desmarca.

Este test fue adaptado para reflejar la nueva funcionalidad implementada donde el usuario puede seleccionar ejercicios mediante checkboxes agrupados por grupo muscular.

### Como correr los test?
''' npm test '''
ejecuta todos los tests del frontend y muestra que todas las suites de Angular pasan correctamente.

''' ./mvnw test '''
ejecuta los tests del backend, incluyendo el test personalizado, todos completándose con éxito.

# Test E2E
