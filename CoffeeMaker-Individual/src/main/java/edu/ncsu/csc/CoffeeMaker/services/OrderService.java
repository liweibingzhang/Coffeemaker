package edu.ncsu.csc.CoffeeMaker.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.CoffeeMaker.models.CoffeemakerOrder;
import edu.ncsu.csc.CoffeeMaker.repositories.OrderRepository;

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
public class OrderService extends Service<CoffeemakerOrder, Long> {
    /**
     * IngredientService, to be autowired in by Spring and provide CRUD
     * operations on Inventory model.
     */
    @Autowired
    private OrderRepository orderRepository;

    /**
     * Returns the ingredient repository
     *
     * @return Returns the ingredient repository
     */
    @Override
    protected JpaRepository<CoffeemakerOrder, Long> getRepository () {
        return orderRepository;
    }

    /**
     * Find a Ingredient with the provided name
     *
     * @param name
     *            Name of the Ingredient to find
     * @return found Ingredient, null if none
     */
    public CoffeemakerOrder findByName ( final String name ) {
        return orderRepository.findByName( name );
    }

    /**
     * Find a Ingredient with the provided name
     *
     * @param name
     *            Name of the Ingredient to find
     * @return found Ingredient, null if none
     */
    @Override
    public CoffeemakerOrder findById ( final Long id ) {
        Optional<CoffeemakerOrder> optionalOrder = null;
        try {
            optionalOrder = orderRepository.findById( id );
            return optionalOrder.get();
        }
        catch ( final Exception ex ) {
            return null;
        }

    }
}
