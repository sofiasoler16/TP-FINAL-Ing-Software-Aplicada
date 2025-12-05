package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.WorkoutAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Workout;
import com.mycompany.myapp.repository.WorkoutRepository;
import com.mycompany.myapp.service.WorkoutService;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link WorkoutResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class WorkoutResourceIT {

    private static final LocalDate DEFAULT_WORKOUT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_WORKOUT_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Integer DEFAULT_DURATION_MINUTES = 1;
    private static final Integer UPDATED_DURATION_MINUTES = 2;

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/workouts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private WorkoutRepository workoutRepository;

    @Mock
    private WorkoutRepository workoutRepositoryMock;

    @Mock
    private WorkoutService workoutServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWorkoutMockMvc;

    private Workout workout;

    private Workout insertedWorkout;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Workout createEntity() {
        return new Workout().workoutDate(DEFAULT_WORKOUT_DATE).durationMinutes(DEFAULT_DURATION_MINUTES).notes(DEFAULT_NOTES);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Workout createUpdatedEntity() {
        return new Workout().workoutDate(UPDATED_WORKOUT_DATE).durationMinutes(UPDATED_DURATION_MINUTES).notes(UPDATED_NOTES);
    }

    @BeforeEach
    void initTest() {
        workout = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedWorkout != null) {
            workoutRepository.delete(insertedWorkout);
            insertedWorkout = null;
        }
    }

    @Test
    @Transactional
    void createWorkout() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Workout
        var returnedWorkout = om.readValue(
            restWorkoutMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(workout)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Workout.class
        );

        // Validate the Workout in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertWorkoutUpdatableFieldsEquals(returnedWorkout, getPersistedWorkout(returnedWorkout));

        insertedWorkout = returnedWorkout;
    }

    @Test
    @Transactional
    void createWorkoutWithExistingId() throws Exception {
        // Create the Workout with an existing ID
        workout.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restWorkoutMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(workout)))
            .andExpect(status().isBadRequest());

        // Validate the Workout in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkWorkoutDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        workout.setWorkoutDate(null);

        // Create the Workout, which fails.

        restWorkoutMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(workout)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllWorkouts() throws Exception {
        // Initialize the database
        insertedWorkout = workoutRepository.saveAndFlush(workout);

        // Get all the workoutList
        restWorkoutMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(workout.getId().intValue())))
            .andExpect(jsonPath("$.[*].workoutDate").value(hasItem(DEFAULT_WORKOUT_DATE.toString())))
            .andExpect(jsonPath("$.[*].durationMinutes").value(hasItem(DEFAULT_DURATION_MINUTES)))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllWorkoutsWithEagerRelationshipsIsEnabled() throws Exception {
        when(workoutServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restWorkoutMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(workoutServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllWorkoutsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(workoutServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restWorkoutMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(workoutRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getWorkout() throws Exception {
        // Initialize the database
        insertedWorkout = workoutRepository.saveAndFlush(workout);

        // Get the workout
        restWorkoutMockMvc
            .perform(get(ENTITY_API_URL_ID, workout.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(workout.getId().intValue()))
            .andExpect(jsonPath("$.workoutDate").value(DEFAULT_WORKOUT_DATE.toString()))
            .andExpect(jsonPath("$.durationMinutes").value(DEFAULT_DURATION_MINUTES))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES));
    }

    @Test
    @Transactional
    void getNonExistingWorkout() throws Exception {
        // Get the workout
        restWorkoutMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingWorkout() throws Exception {
        // Initialize the database
        insertedWorkout = workoutRepository.saveAndFlush(workout);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the workout
        Workout updatedWorkout = workoutRepository.findById(workout.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedWorkout are not directly saved in db
        em.detach(updatedWorkout);
        updatedWorkout.workoutDate(UPDATED_WORKOUT_DATE).durationMinutes(UPDATED_DURATION_MINUTES).notes(UPDATED_NOTES);

        restWorkoutMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedWorkout.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedWorkout))
            )
            .andExpect(status().isOk());

        // Validate the Workout in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedWorkoutToMatchAllProperties(updatedWorkout);
    }

    @Test
    @Transactional
    void putNonExistingWorkout() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        workout.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWorkoutMockMvc
            .perform(put(ENTITY_API_URL_ID, workout.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(workout)))
            .andExpect(status().isBadRequest());

        // Validate the Workout in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchWorkout() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        workout.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkoutMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(workout))
            )
            .andExpect(status().isBadRequest());

        // Validate the Workout in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamWorkout() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        workout.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkoutMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(workout)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Workout in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateWorkoutWithPatch() throws Exception {
        // Initialize the database
        insertedWorkout = workoutRepository.saveAndFlush(workout);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the workout using partial update
        Workout partialUpdatedWorkout = new Workout();
        partialUpdatedWorkout.setId(workout.getId());

        partialUpdatedWorkout.workoutDate(UPDATED_WORKOUT_DATE).notes(UPDATED_NOTES);

        restWorkoutMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWorkout.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedWorkout))
            )
            .andExpect(status().isOk());

        // Validate the Workout in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWorkoutUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedWorkout, workout), getPersistedWorkout(workout));
    }

    @Test
    @Transactional
    void fullUpdateWorkoutWithPatch() throws Exception {
        // Initialize the database
        insertedWorkout = workoutRepository.saveAndFlush(workout);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the workout using partial update
        Workout partialUpdatedWorkout = new Workout();
        partialUpdatedWorkout.setId(workout.getId());

        partialUpdatedWorkout.workoutDate(UPDATED_WORKOUT_DATE).durationMinutes(UPDATED_DURATION_MINUTES).notes(UPDATED_NOTES);

        restWorkoutMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWorkout.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedWorkout))
            )
            .andExpect(status().isOk());

        // Validate the Workout in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWorkoutUpdatableFieldsEquals(partialUpdatedWorkout, getPersistedWorkout(partialUpdatedWorkout));
    }

    @Test
    @Transactional
    void patchNonExistingWorkout() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        workout.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWorkoutMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, workout.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(workout))
            )
            .andExpect(status().isBadRequest());

        // Validate the Workout in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchWorkout() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        workout.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkoutMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(workout))
            )
            .andExpect(status().isBadRequest());

        // Validate the Workout in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamWorkout() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        workout.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkoutMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(workout)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Workout in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteWorkout() throws Exception {
        // Initialize the database
        insertedWorkout = workoutRepository.saveAndFlush(workout);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the workout
        restWorkoutMockMvc
            .perform(delete(ENTITY_API_URL_ID, workout.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return workoutRepository.count();
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

    protected Workout getPersistedWorkout(Workout workout) {
        return workoutRepository.findById(workout.getId()).orElseThrow();
    }

    protected void assertPersistedWorkoutToMatchAllProperties(Workout expectedWorkout) {
        assertWorkoutAllPropertiesEquals(expectedWorkout, getPersistedWorkout(expectedWorkout));
    }

    protected void assertPersistedWorkoutToMatchUpdatableProperties(Workout expectedWorkout) {
        assertWorkoutAllUpdatablePropertiesEquals(expectedWorkout, getPersistedWorkout(expectedWorkout));
    }
}
