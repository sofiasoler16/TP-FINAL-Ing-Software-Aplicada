package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Workout.
 */
@Entity
@Table(name = "workout")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Workout implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "workout_date", nullable = false)
    private LocalDate workoutDate;

    @Min(value = 1)
    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Size(max = 1000)
    @Column(name = "notes", length = 1000)
    private String notes;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_workout__exercises",
        joinColumns = @JoinColumn(name = "workout_id"),
        inverseJoinColumns = @JoinColumn(name = "exercises_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "workouts" }, allowSetters = true)
    private Set<Exercise> exercises = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Workout id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getWorkoutDate() {
        return this.workoutDate;
    }

    public Workout workoutDate(LocalDate workoutDate) {
        this.setWorkoutDate(workoutDate);
        return this;
    }

    public void setWorkoutDate(LocalDate workoutDate) {
        this.workoutDate = workoutDate;
    }

    public Integer getDurationMinutes() {
        return this.durationMinutes;
    }

    public Workout durationMinutes(Integer durationMinutes) {
        this.setDurationMinutes(durationMinutes);
        return this;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getNotes() {
        return this.notes;
    }

    public Workout notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Set<Exercise> getExercises() {
        return this.exercises;
    }

    public void setExercises(Set<Exercise> exercises) {
        this.exercises = exercises;
    }

    public Workout exercises(Set<Exercise> exercises) {
        this.setExercises(exercises);
        return this;
    }

    public Workout addExercises(Exercise exercise) {
        this.exercises.add(exercise);
        return this;
    }

    public Workout removeExercises(Exercise exercise) {
        this.exercises.remove(exercise);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Workout)) {
            return false;
        }
        return getId() != null && getId().equals(((Workout) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Workout{" +
            "id=" + getId() +
            ", workoutDate='" + getWorkoutDate() + "'" +
            ", durationMinutes=" + getDurationMinutes() +
            ", notes='" + getNotes() + "'" +
            "}";
    }
}
