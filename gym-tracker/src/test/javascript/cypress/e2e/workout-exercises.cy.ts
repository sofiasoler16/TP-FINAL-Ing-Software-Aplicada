// src/test/javascript/cypress/e2e/workout-exercises.cy.ts

describe('Workout - Exercises E2E', () => {
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const workoutPageUrl = '/workout';
  const exercisePageUrl = '/exercise';

  beforeEach(() => {
    cy.login(username, password);
  });

  it('should display exercises as checkboxes and allow selecting one', () => {
    //
    // 1) Crear al menos un Exercise usando la UI (igual estilo que entity/exercise.cy.ts)
    //
    cy.visit(exercisePageUrl);

    cy.get('[data-cy="entityCreateButton"]').click();

    // name
    cy.get('[data-cy="name"]').clear().type('Sentadilla Cypress');

    // muscleGroup (elige cualquier opción válida)
    cy.get('[data-cy="muscleGroup"]').select(1);

    // difficulty (campo obligatorio según el backend)
    cy.get('[data-cy="difficulty"]').select(1);

    // Guardar el ejercicio
    cy.get('[data-cy="entityCreateSaveButton"]').click();

    // Verificamos que volvemos al listado de Exercises
    cy.url().should('include', exercisePageUrl);
    cy.get('#page-heading').should('contain.text', 'Exercises');

    //
    // 2) Ir a la pantalla de Workouts y abrir el formulario de creación
    //
    cy.visit(workoutPageUrl);
    cy.get('[data-cy="entityCreateButton"]').click();

    //
    // 3) Ver que la sección "Exercises" exista
    //
    cy.contains('Exercises').should('exist');

    //
    // 4) Buscar los checkboxes dentro del bloque de Exercises
    //
    cy.contains('Exercises')
      .parent() // el <div class="mb-3"> que envuelve la sección de ejercicios
      .find('input.form-check-input[type="checkbox"]', { timeout: 10000 })
      .should('have.length.at.least', 1)
      .first()
      .as('firstCheckbox');

    // 5) Ver que se pueda tildar
    cy.get('@firstCheckbox').should('not.be.checked');
    cy.get('@firstCheckbox').check({ force: true });
    cy.get('@firstCheckbox').should('be.checked');

    // No tocamos el botón Save del Workout, solo verificamos la UI de ejercicios.
  });
});
