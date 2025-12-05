package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.ExerciseAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Exercise;
import com.mycompany.myapp.domain.enumeration.Difficulty;
import com.mycompany.myapp.domain.enumeration.MuscleGroup;
import com.mycompany.myapp.repository.ExerciseRepository;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ExerciseResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ExerciseResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final MuscleGroup DEFAULT_MUSCLE_GROUP = MuscleGroup.CHEST;
    private static final MuscleGroup UPDATED_MUSCLE_GROUP = MuscleGroup.BACK;

    private static final Difficulty DEFAULT_DIFFICULTY = Difficulty.EASY;
    private static final Difficulty UPDATED_DIFFICULTY = Difficulty.MEDIUM;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/exercises";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restExerciseMockMvc;

    private Exercise exercise;

    private Exercise insertedExercise;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Exercise createEntity() {
        return new Exercise()
            .name(DEFAULT_NAME)
            .muscleGroup(DEFAULT_MUSCLE_GROUP)
            .difficulty(DEFAULT_DIFFICULTY)
            .description(DEFAULT_DESCRIPTION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Exercise createUpdatedEntity() {
        return new Exercise()
            .name(UPDATED_NAME)
            .muscleGroup(UPDATED_MUSCLE_GROUP)
            .difficulty(UPDATED_DIFFICULTY)
            .description(UPDATED_DESCRIPTION);
    }

    @BeforeEach
    void initTest() {
        exercise = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedExercise != null) {
            exerciseRepository.delete(insertedExercise);
            insertedExercise = null;
        }
    }

    @Test
    @Transactional
    void createExercise() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Exercise
        var returnedExercise = om.readValue(
            restExerciseMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(exercise)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Exercise.class
        );

        // Validate the Exercise in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertExerciseUpdatableFieldsEquals(returnedExercise, getPersistedExercise(returnedExercise));

        insertedExercise = returnedExercise;
    }

    @Test
    @Transactional
    void createExerciseWithExistingId() throws Exception {
        // Create the Exercise with an existing ID
        exercise.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restExerciseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(exercise)))
            .andExpect(status().isBadRequest());

        // Validate the Exercise in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        exercise.setName(null);

        // Create the Exercise, which fails.

        restExerciseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(exercise)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMuscleGroupIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        exercise.setMuscleGroup(null);

        // Create the Exercise, which fails.

        restExerciseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(exercise)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDifficultyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        exercise.setDifficulty(null);

        // Create the Exercise, which fails.

        restExerciseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(exercise)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllExercises() throws Exception {
        // Initialize the database
        insertedExercise = exerciseRepository.saveAndFlush(exercise);

        // Get all the exerciseList
        restExerciseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(exercise.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].muscleGroup").value(hasItem(DEFAULT_MUSCLE_GROUP.toString())))
            .andExpect(jsonPath("$.[*].difficulty").value(hasItem(DEFAULT_DIFFICULTY.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getExercise() throws Exception {
        // Initialize the database
        insertedExercise = exerciseRepository.saveAndFlush(exercise);

        // Get the exercise
        restExerciseMockMvc
            .perform(get(ENTITY_API_URL_ID, exercise.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(exercise.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.muscleGroup").value(DEFAULT_MUSCLE_GROUP.toString()))
            .andExpect(jsonPath("$.difficulty").value(DEFAULT_DIFFICULTY.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingExercise() throws Exception {
        // Get the exercise
        restExerciseMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingExercise() throws Exception {
        // Initialize the database
        insertedExercise = exerciseRepository.saveAndFlush(exercise);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the exercise
        Exercise updatedExercise = exerciseRepository.findById(exercise.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedExercise are not directly saved in db
        em.detach(updatedExercise);
        updatedExercise
            .name(UPDATED_NAME)
            .muscleGroup(UPDATED_MUSCLE_GROUP)
            .difficulty(UPDATED_DIFFICULTY)
            .description(UPDATED_DESCRIPTION);

        restExerciseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedExercise.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedExercise))
            )
            .andExpect(status().isOk());

        // Validate the Exercise in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedExerciseToMatchAllProperties(updatedExercise);
    }

    @Test
    @Transactional
    void putNonExistingExercise() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        exercise.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExerciseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, exercise.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(exercise))
            )
            .andExpect(status().isBadRequest());

        // Validate the Exercise in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchExercise() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        exercise.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExerciseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(exercise))
            )
            .andExpect(status().isBadRequest());

        // Validate the Exercise in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamExercise() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        exercise.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExerciseMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(exercise)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Exercise in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateExerciseWithPatch() throws Exception {
        // Initialize the database
        insertedExercise = exerciseRepository.saveAndFlush(exercise);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the exercise using partial update
        Exercise partialUpdatedExercise = new Exercise();
        partialUpdatedExercise.setId(exercise.getId());

        partialUpdatedExercise.difficulty(UPDATED_DIFFICULTY);

        restExerciseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExercise.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedExercise))
            )
            .andExpect(status().isOk());

        // Validate the Exercise in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertExerciseUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedExercise, exercise), getPersistedExercise(exercise));
    }

    @Test
    @Transactional
    void fullUpdateExerciseWithPatch() throws Exception {
        // Initialize the database
        insertedExercise = exerciseRepository.saveAndFlush(exercise);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the exercise using partial update
        Exercise partialUpdatedExercise = new Exercise();
        partialUpdatedExercise.setId(exercise.getId());

        partialUpdatedExercise
            .name(UPDATED_NAME)
            .muscleGroup(UPDATED_MUSCLE_GROUP)
            .difficulty(UPDATED_DIFFICULTY)
            .description(UPDATED_DESCRIPTION);

        restExerciseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExercise.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedExercise))
            )
            .andExpect(status().isOk());

        // Validate the Exercise in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertExerciseUpdatableFieldsEquals(partialUpdatedExercise, getPersistedExercise(partialUpdatedExercise));
    }

    @Test
    @Transactional
    void patchNonExistingExercise() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        exercise.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExerciseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, exercise.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(exercise))
            )
            .andExpect(status().isBadRequest());

        // Validate the Exercise in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchExercise() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        exercise.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExerciseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(exercise))
            )
            .andExpect(status().isBadRequest());

        // Validate the Exercise in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamExercise() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        exercise.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExerciseMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(exercise)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Exercise in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteExercise() throws Exception {
        // Initialize the database
        insertedExercise = exerciseRepository.saveAndFlush(exercise);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the exercise
        restExerciseMockMvc
            .perform(delete(ENTITY_API_URL_ID, exercise.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return exerciseRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Exercise getPersistedExercise(Exercise exercise) {
        return exerciseRepository.findById(exercise.getId()).orElseThrow();
    }

    protected void assertPersistedExerciseToMatchAllProperties(Exercise expectedExercise) {
        assertExerciseAllPropertiesEquals(expectedExercise, getPersistedExercise(expectedExercise));
    }

    protected void assertPersistedExerciseToMatchUpdatableProperties(Exercise expectedExercise) {
        assertExerciseAllUpdatablePropertiesEquals(expectedExercise, getPersistedExercise(expectedExercise));
    }
}
