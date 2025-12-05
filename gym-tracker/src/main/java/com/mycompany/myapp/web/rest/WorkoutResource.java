package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Workout;
import com.mycompany.myapp.repository.WorkoutRepository;
import com.mycompany.myapp.service.WorkoutService;
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
import org.springframework.web.bind.annotation.CrossOrigin;


/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Workout}.
 */
@RestController
@RequestMapping("/api/workouts")
@CrossOrigin(origins = "http://localhost:8100")
public class WorkoutResource {

    private static final Logger LOG = LoggerFactory.getLogger(WorkoutResource.class);

    private static final String ENTITY_NAME = "workout";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WorkoutService workoutService;

    private final WorkoutRepository workoutRepository;

    public WorkoutResource(WorkoutService workoutService, WorkoutRepository workoutRepository) {
        this.workoutService = workoutService;
        this.workoutRepository = workoutRepository;
    }

    /**
     * {@code POST  /workouts} : Create a new workout.
     *
     * @param workout the workout to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new workout, or with status {@code 400 (Bad Request)} if the workout has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Workout> createWorkout(@Valid @RequestBody Workout workout) throws URISyntaxException {
        LOG.debug("REST request to save Workout : {}", workout);
        if (workout.getId() != null) {
            throw new BadRequestAlertException("A new workout cannot already have an ID", ENTITY_NAME, "idexists");
        }
        workout = workoutService.save(workout);
        return ResponseEntity.created(new URI("/api/workouts/" + workout.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, workout.getId().toString()))
            .body(workout);
    }

    /**
     * {@code PUT  /workouts/:id} : Updates an existing workout.
     *
     * @param id the id of the workout to save.
     * @param workout the workout to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated workout,
     * or with status {@code 400 (Bad Request)} if the workout is not valid,
     * or with status {@code 500 (Internal Server Error)} if the workout couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Workout> updateWorkout(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Workout workout
    ) throws URISyntaxException {
        LOG.debug("REST request to update Workout : {}, {}", id, workout);
        if (workout.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, workout.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!workoutRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        workout = workoutService.update(workout);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, workout.getId().toString()))
            .body(workout);
    }

    /**
     * {@code PATCH  /workouts/:id} : Partial updates given fields of an existing workout, field will ignore if it is null
     *
     * @param id the id of the workout to save.
     * @param workout the workout to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated workout,
     * or with status {@code 400 (Bad Request)} if the workout is not valid,
     * or with status {@code 404 (Not Found)} if the workout is not found,
     * or with status {@code 500 (Internal Server Error)} if the workout couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Workout> partialUpdateWorkout(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Workout workout
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Workout partially : {}, {}", id, workout);
        if (workout.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, workout.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!workoutRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Workout> result = workoutService.partialUpdate(workout);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, workout.getId().toString())
        );
    }

    /**
     * {@code GET  /workouts} : get all the workouts.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of workouts in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Workout>> getAllWorkouts(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of Workouts");
        Page<Workout> page;
        if (eagerload) {
            page = workoutService.findAllWithEagerRelationships(pageable);
        } else {
            page = workoutService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /workouts/:id} : get the "id" workout.
     *
     * @param id the id of the workout to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the workout, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Workout> getWorkout(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Workout : {}", id);
        Optional<Workout> workout = workoutService.findOne(id);
        return ResponseUtil.wrapOrNotFound(workout);
    }

    /**
     * {@code DELETE  /workouts/:id} : delete the "id" workout.
     *
     * @param id the id of the workout to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkout(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Workout : {}", id);
        workoutService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
