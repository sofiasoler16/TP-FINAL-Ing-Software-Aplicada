import {
  entityConfirmDeleteButtonSelector,
  entityCreateButtonSelector,
  entityCreateCancelButtonSelector,
  entityCreateSaveButtonSelector,
  entityDeleteButtonSelector,
  entityDetailsBackButtonSelector,
  entityDetailsButtonSelector,
  entityEditButtonSelector,
  entityTableSelector,
} from '../../support/entity';

describe('Workout e2e test', () => {
  const workoutPageUrl = '/workout';
  const workoutPageUrlPattern = new RegExp('/workout(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const workoutSample = { workoutDate: '2025-11-30' };

  let workout;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/workouts+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/workouts').as('postEntityRequest');
    cy.intercept('DELETE', '/api/workouts/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (workout) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/workouts/${workout.id}`,
      }).then(() => {
        workout = undefined;
      });
    }
  });

  it('Workouts menu should load Workouts page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('workout');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Workout').should('exist');
    cy.url().should('match', workoutPageUrlPattern);
  });

  describe('Workout page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(workoutPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Workout page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/workout/new$'));
        cy.getEntityCreateUpdateHeading('Workout');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', workoutPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/workouts',
          body: workoutSample,
        }).then(({ body }) => {
          workout = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/workouts+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/workouts?page=0&size=20>; rel="last",<http://localhost/api/workouts?page=0&size=20>; rel="first"',
              },
              body: [workout],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(workoutPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Workout page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('workout');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', workoutPageUrlPattern);
      });

      it('edit button click should load edit Workout page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Workout');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', workoutPageUrlPattern);
      });

      it('edit button click should load edit Workout page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Workout');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', workoutPageUrlPattern);
      });

      it('last delete button click should delete instance of Workout', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('workout').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', workoutPageUrlPattern);

        workout = undefined;
      });
    });
  });

  describe('new Workout page', () => {
    beforeEach(() => {
      cy.visit(`${workoutPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Workout');
    });

    it('should create an instance of Workout', () => {
      cy.get(`[data-cy="workoutDate"]`).type('2025-11-30');
      cy.get(`[data-cy="workoutDate"]`).blur();
      cy.get(`[data-cy="workoutDate"]`).should('have.value', '2025-11-30');

      cy.get(`[data-cy="durationMinutes"]`).type('28827');
      cy.get(`[data-cy="durationMinutes"]`).should('have.value', '28827');

      cy.get(`[data-cy="notes"]`).type('colorize');
      cy.get(`[data-cy="notes"]`).should('have.value', 'colorize');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        workout = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', workoutPageUrlPattern);
    });
  });
});
