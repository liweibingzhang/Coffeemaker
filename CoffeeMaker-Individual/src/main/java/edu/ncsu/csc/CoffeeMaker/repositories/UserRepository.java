package edu.ncsu.csc.CoffeeMaker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.CoffeeMaker.models.CoffeemakerUser;

/**
 * RecipeRepository is used to provide CRUD operations for the Recipe model.
 * Spring will generate appropriate code with JPA.
 *
 * @author Kai Presler-Marshall
 *
 */
public interface UserRepository extends JpaRepository<CoffeemakerUser, Long> {

    /**
     * Finds a Recipe object with the provided name. Spring will generate code
     * to make this happen.
     *
     * @param name
     *            Name of the recipe
     * @return Found recipe, null if none.
     */
    CoffeemakerUser findByName ( String name );

}
