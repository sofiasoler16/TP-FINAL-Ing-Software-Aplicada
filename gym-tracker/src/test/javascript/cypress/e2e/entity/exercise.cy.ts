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

describe('Exercise e2e test', () => {
  const exercisePageUrl = '/exercise';
  const exercisePageUrlPattern = new RegExp('/exercise(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const exerciseSample = { name: 'firsthand', muscleGroup: 'GLUTES', difficulty: 'HARD' };

  let exercise;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/exercises+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/exercises').as('postEntityRequest');
    cy.intercept('DELETE', '/api/exercises/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (exercise) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/exercises/${exercise.id}`,
      }).then(() => {
        exercise = undefined;
      });
    }
  });

  it('Exercises menu should load Exercises page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('exercise');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Exercise').should('exist');
    cy.url().should('match', exercisePageUrlPattern);
  });

  describe('Exercise page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(exercisePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Exercise page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/exercise/new$'));
        cy.getEntityCreateUpdateHeading('Exercise');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', exercisePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/exercises',
          body: exerciseSample,
        }).then(({ body }) => {
          exercise = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/exercises+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/exercises?page=0&size=20>; rel="last",<http://localhost/api/exercises?page=0&size=20>; rel="first"',
              },
              body: [exercise],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(exercisePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Exercise page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('exercise');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', exercisePageUrlPattern);
      });

      it('edit button click should load edit Exercise page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Exercise');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', exercisePageUrlPattern);
      });

      it('edit button click should load edit Exercise page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Exercise');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', exercisePageUrlPattern);
      });

      it('last delete button click should delete instance of Exercise', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('exercise').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', exercisePageUrlPattern);

        exercise = undefined;
      });
    });
  });

  describe('new Exercise page', () => {
    beforeEach(() => {
      cy.visit(`${exercisePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Exercise');
    });

    it('should create an instance of Exercise', () => {
      cy.get(`[data-cy="name"]`).type('near mostly jot');
      cy.get(`[data-cy="name"]`).should('have.value', 'near mostly jot');

      cy.get(`[data-cy="muscleGroup"]`).select('LEGS');

      cy.get(`[data-cy="difficulty"]`).select('HARD');

      cy.get(`[data-cy="description"]`).type('simple aha');
      cy.get(`[data-cy="description"]`).should('have.value', 'simple aha');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        exercise = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', exercisePageUrlPattern);
    });
  });
});
