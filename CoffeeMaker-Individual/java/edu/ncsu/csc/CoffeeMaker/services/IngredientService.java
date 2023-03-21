package edu.ncsu.csc.CoffeeMaker.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.repositories.IngredientRepository;

/**
 *
 * The IngredientService is used to handle CRUD operations on the Ingredient
 * model.
 *
 * @author Shiva Ganapathy
 *
 * 
 *
 */
@Component
@Transactional
public class IngredientService extends Service {
    /**
     * IngredientService, to be autowired in by Spring and provide CRUD
     * operations on Inventory model.
     */
    @Autowired
    private IngredientRepository ingredientRepository;

    /**
     * Returns the ingredient repository
     *
     * @return Returns the ingredient repository
     */
    @Override
    protected JpaRepository<Ingredient, Long> getRepository () {
        return ingredientRepository;
    }

    /**
     * Find a Ingredient with the provided name
     *
     * @param name
     *            Name of the Ingredient to find
     * @return found Ingredient, null if none
     */
    public Ingredient findByName ( final String name ) {
        return ingredientRepository.findByName( name );
    }
}
