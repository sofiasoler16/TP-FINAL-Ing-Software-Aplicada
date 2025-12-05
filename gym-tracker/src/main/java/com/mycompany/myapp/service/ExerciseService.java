package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Exercise;
import com.mycompany.myapp.repository.ExerciseRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Exercise}.
 */
@Service
@Transactional
public class ExerciseService {

    private static final Logger LOG = LoggerFactory.getLogger(ExerciseService.class);

    private final ExerciseRepository exerciseRepository;

    public ExerciseService(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    /**
     * Save a exercise.
     *
     * @param exercise the entity to save.
     * @return the persisted entity.
     */
    public Exercise save(Exercise exercise) {
        LOG.debug("Request to save Exercise : {}", exercise);
        return exerciseRepository.save(exercise);
    }

    /**
     * Update a exercise.
     *
     * @param exercise the entity to save.
     * @return the persisted entity.
     */
    public Exercise update(Exercise exercise) {
        LOG.debug("Request to update Exercise : {}", exercise);
        return exerciseRepository.save(exercise);
    }

    /**
     * Partially update a exercise.
     *
     * @param exercise the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Exercise> partialUpdate(Exercise exercise) {
        LOG.debug("Request to partially update Exercise : {}", exercise);

        return exerciseRepository
            .findById(exercise.getId())
            .map(existingExercise -> {
                if (exercise.getName() != null) {
                    existingExercise.setName(exercise.getName());
                }
                if (exercise.getMuscleGroup() != null) {
                    existingExercise.setMuscleGroup(exercise.getMuscleGroup());
                }
                if (exercise.getDifficulty() != null) {
                    existingExercise.setDifficulty(exercise.getDifficulty());
                }
                if (exercise.getDescription() != null) {
                    existingExercise.setDescription(exercise.getDescription());
                }

                return existingExercise;
            })
            .map(exerciseRepository::save);
    }

    /**
     * Get all the exercises.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Exercise> findAll(Pageable pageable) {
        LOG.debug("Request to get all Exercises");
        return exerciseRepository.findAll(pageable);
    }

    /**
     * Get one exercise by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Exercise> findOne(Long id) {
        LOG.debug("Request to get Exercise : {}", id);
        return exerciseRepository.findById(id);
    }

    /**
     * Delete the exercise by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Exercise : {}", id);
        exerciseRepository.deleteById(id);
    }
}
