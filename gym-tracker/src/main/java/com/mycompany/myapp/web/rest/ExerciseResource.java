package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Exercise;
import com.mycompany.myapp.repository.ExerciseRepository;
import com.mycompany.myapp.service.ExerciseService;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Exercise}.
 */
@RestController
@RequestMapping("/api/exercises")
public class ExerciseResource {

    private static final Logger LOG = LoggerFactory.getLogger(ExerciseResource.class);

    private static final String ENTITY_NAME = "exercise";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ExerciseService exerciseService;

    private final ExerciseRepository exerciseRepository;

    public ExerciseResource(ExerciseService exerciseService, ExerciseRepository exerciseRepository) {
        this.exerciseService = exerciseService;
        this.exerciseRepository = exerciseRepository;
    }

    /**
     * {@code POST  /exercises} : Create a new exercise.
     *
     * @param exercise the exercise to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new exercise, or with status {@code 400 (Bad Request)} if the exercise has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Exercise> createExercise(@Valid @RequestBody Exercise exercise) throws URISyntaxException {
        LOG.debug("REST request to save Exercise : {}", exercise);
        if (exercise.getId() != null) {
            throw new BadRequestAlertException("A new exercise cannot already have an ID", ENTITY_NAME, "idexists");
        }
        exercise = exerciseService.save(exercise);
        return ResponseEntity.created(new URI("/api/exercises/" + exercise.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, exercise.getId().toString()))
            .body(exercise);
    }

    /**
     * {@code PUT  /exercises/:id} : Updates an existing exercise.
     *
     * @param id the id of the exercise to save.
     * @param exercise the exercise to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated exercise,
     * or with status {@code 400 (Bad Request)} if the exercise is not valid,
     * or with status {@code 500 (Internal Server Error)} if the exercise couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Exercise> updateExercise(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Exercise exercise
    ) throws URISyntaxException {
        LOG.debug("REST request to update Exercise : {}, {}", id, exercise);
        if (exercise.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, exercise.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!exerciseRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        exercise = exerciseService.update(exercise);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, exercise.getId().toString()))
            .body(exercise);
    }

    /**
     * {@code PATCH  /exercises/:id} : Partial updates given fields of an existing exercise, field will ignore if it is null
     *
     * @param id the id of the exercise to save.
     * @param exercise the exercise to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated exercise,
     * or with status {@code 400 (Bad Request)} if the exercise is not valid,
     * or with status {@code 404 (Not Found)} if the exercise is not found,
     * or with status {@code 500 (Internal Server Error)} if the exercise couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Exercise> partialUpdateExercise(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Exercise exercise
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Exercise partially : {}, {}", id, exercise);
        if (exercise.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, exercise.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!exerciseRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Exercise> result = exerciseService.partialUpdate(exercise);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, exercise.getId().toString())
        );
    }

    /**
     * {@code GET  /exercises} : get all the exercises.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of exercises in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Exercise>> getAllExercises(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Exercises");
        Page<Exercise> page = exerciseService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /exercises/:id} : get the "id" exercise.
     *
     * @param id the id of the exercise to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the exercise, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Exercise> getExercise(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Exercise : {}", id);
        Optional<Exercise> exercise = exerciseService.findOne(id);
        return ResponseUtil.wrapOrNotFound(exercise);
    }

    /**
     * {@code DELETE  /exercises/:id} : delete the "id" exercise.
     *
     * @param id the id of the exercise to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExercise(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Exercise : {}", id);
        exerciseService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
