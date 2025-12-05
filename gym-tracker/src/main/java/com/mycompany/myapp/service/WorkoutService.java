package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Workout;
import com.mycompany.myapp.repository.WorkoutRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Workout}.
 */
@Service
@Transactional
public class WorkoutService {

    private static final Logger LOG = LoggerFactory.getLogger(WorkoutService.class);

    private final WorkoutRepository workoutRepository;

    public WorkoutService(WorkoutRepository workoutRepository) {
        this.workoutRepository = workoutRepository;
    }

    /**
     * Save a workout.
     *
     * @param workout the entity to save.
     * @return the persisted entity.
     */
    public Workout save(Workout workout) {
        LOG.debug("Request to save Workout : {}", workout);
        return workoutRepository.save(workout);
    }

    /**
     * Update a workout.
     *
     * @param workout the entity to save.
     * @return the persisted entity.
     */
    public Workout update(Workout workout) {
        LOG.debug("Request to update Workout : {}", workout);
        return workoutRepository.save(workout);
    }

    /**
     * Partially update a workout.
     *
     * @param workout the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Workout> partialUpdate(Workout workout) {
        LOG.debug("Request to partially update Workout : {}", workout);

        return workoutRepository
            .findById(workout.getId())
            .map(existingWorkout -> {
                if (workout.getWorkoutDate() != null) {
                    existingWorkout.setWorkoutDate(workout.getWorkoutDate());
                }
                if (workout.getDurationMinutes() != null) {
                    existingWorkout.setDurationMinutes(workout.getDurationMinutes());
                }
                if (workout.getNotes() != null) {
                    existingWorkout.setNotes(workout.getNotes());
                }

                return existingWorkout;
            })
            .map(workoutRepository::save);
    }

    /**
     * Get all the workouts.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Workout> findAll(Pageable pageable) {
        LOG.debug("Request to get all Workouts");
        return workoutRepository.findAll(pageable);
    }

    /**
     * Get all the workouts with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<Workout> findAllWithEagerRelationships(Pageable pageable) {
        return workoutRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one workout by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Workout> findOne(Long id) {
        LOG.debug("Request to get Workout : {}", id);
        return workoutRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the workout by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Workout : {}", id);
        workoutRepository.deleteById(id);
    }
}
