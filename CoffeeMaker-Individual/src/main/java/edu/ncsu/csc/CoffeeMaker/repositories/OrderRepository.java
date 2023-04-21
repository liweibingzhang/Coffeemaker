package edu.ncsu.csc.CoffeeMaker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.CoffeeMaker.models.CoffeemakerOrder;

/**
 * Interface for the database, used by the Spring Boot framework. Allows saving
 * and retrieving Ingredients.
 *
 * @author Osama Albahrani (osalbahr)
 * @author Mathew Chanda
 *
 */
public interface OrderRepository extends JpaRepository<CoffeemakerOrder, Long> {
    /**
     * Finds a Ingredient object with the provided name. Spring will generate
     * code to make this happen.
     *
     * @param name
     *            Name of the Ingredient
     * @return Found recipe, null if none.
     */

    CoffeemakerOrder findByName ( String name );
}
