package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.myapp.domain.enumeration.Difficulty;
import com.mycompany.myapp.domain.enumeration.MuscleGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * ENTIDADES
 */
@Schema(description = "ENTIDADES")
@Entity
@Table(name = "exercise")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Exercise implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "muscle_group", nullable = false)
    private MuscleGroup muscleGroup;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", nullable = false)
    private Difficulty difficulty;

    @Size(max = 1000)
    @Column(name = "description", length = 1000)
    private String description;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "exercises")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "exercises" }, allowSetters = true)
    private Set<Workout> workouts = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Exercise id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Exercise name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MuscleGroup getMuscleGroup() {
        return this.muscleGroup;
    }

    public Exercise muscleGroup(MuscleGroup muscleGroup) {
        this.setMuscleGroup(muscleGroup);
        return this;
    }

    public void setMuscleGroup(MuscleGroup muscleGroup) {
        this.muscleGroup = muscleGroup;
    }

    public Difficulty getDifficulty() {
        return this.difficulty;
    }

    public Exercise difficulty(Difficulty difficulty) {
        this.setDifficulty(difficulty);
        return this;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public String getDescription() {
        return this.description;
    }

    public Exercise description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Workout> getWorkouts() {
        return this.workouts;
    }

    public void setWorkouts(Set<Workout> workouts) {
        if (this.workouts != null) {
            this.workouts.forEach(i -> i.removeExercises(this));
        }
        if (workouts != null) {
            workouts.forEach(i -> i.addExercises(this));
        }
        this.workouts = workouts;
    }

    public Exercise workouts(Set<Workout> workouts) {
        this.setWorkouts(workouts);
        return this;
    }

    public Exercise addWorkouts(Workout workout) {
        this.workouts.add(workout);
        workout.getExercises().add(this);
        return this;
    }

    public Exercise removeWorkouts(Workout workout) {
        this.workouts.remove(workout);
        workout.getExercises().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Exercise)) {
            return false;
        }
        return getId() != null && getId().equals(((Exercise) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Exercise{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", muscleGroup='" + getMuscleGroup() + "'" +
            ", difficulty='" + getDifficulty() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
