package edu.ncsu.csc.CoffeeMaker.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;

/**
 * This is the controller that holds the REST endpoints that handle add and
 * update operations for the Ingredients.
 *
 * Spring will automatically convert all of the ResponseEntity and List results
 * to JSON
 *
 * @author Mathew Chanda
 *
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APIIngredientController extends APIController {

    /**
     * RecipeService object, to be autowired in by Spring to allow for
     * manipulating the Recipe model
     */
    @Autowired
    private IngredientService ingredientService;

    /**
     * Endpoint that gets a specific ingredient by name
     *
     * @param name
     *            - name of the ingredient
     * @return response to the request
     */
    @GetMapping ( BASE_PATH + "ingredient/{name}" )
    public ResponseEntity getIngredient ( @PathVariable final String name ) {

        final Ingredient ingr = ingredientService.findByName( name );

        if ( null == ingr ) {
            return new ResponseEntity( HttpStatus.NOT_FOUND );
        }

        return new ResponseEntity( ingr, HttpStatus.OK );
    }

    /**
     * Endpont that grab all known ingredients
     *
     * @return response to the request
     */
    @GetMapping ( BASE_PATH + "ingredients" )
    public List<Ingredient> getAllIngredients () {
        return ingredientService.findAll();
    }

    /**
     * Endpoint that creates ingredient
     *
     * @param ingredient
     *            - name of ingredient
     * @return response to the request
     */
    @PostMapping ( BASE_PATH + "ingredient" )
    public ResponseEntity createIngredient ( @RequestBody final Ingredient ingredient ) {
        final Ingredient current = ingredientService.findByName( ingredient.getName() );
        if ( current != null ) {
            return new ResponseEntity( current.getName() + "Exists", HttpStatus.CONFLICT );
        }

        ingredientService.save( ingredient );

        System.out.println( "OK Ingredient " + ingredient.getName() );
        return new ResponseEntity( ingredient, HttpStatus.OK );
    }

    /**
     * Endpont that delete ingredient
     *
     * @param name
     *            - name of ingredient
     * @return response to the request
     */
    @DeleteMapping ( BASE_PATH + "ingredient/{name}" )
    public ResponseEntity deleteIngredient ( @PathVariable final String name ) {
        final Ingredient current = ingredientService.findByName( name );
        if ( current == null ) {
            return new ResponseEntity( name + "Does Not Exists", HttpStatus.CONFLICT );
        }

        ingredientService.delete( current );

        return new ResponseEntity( name + "Deleted", HttpStatus.OK );

    }

    /**
     * Endpoint that Update the ingredient
     *
     * @param ingredient
     *            - ingredient that we trying to update
     *
     * @return the response to the request
     */
    @PutMapping ( BASE_PATH + "ingredient" )
    public ResponseEntity updateIngredient ( @RequestBody final Ingredient ingredient ) {
        final Ingredient current = ingredientService.findByName( ingredient.getName() );
        if ( current == null ) {
            return new ResponseEntity( ingredient.getName() + "Does Not Exists", HttpStatus.CONFLICT );
        }

        current.setAmount( ingredient.getAmount() );
        ingredientService.save( current );
        return new ResponseEntity( current.getName() + "Updated", HttpStatus.OK );
    }
}
