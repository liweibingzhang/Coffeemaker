package edu.ncsu.csc.CoffeeMaker.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;

/**
 * This is the controller that holds the REST endpoints that handle add and
 * update operations for the Inventory.
 *
 * Spring will automatically convert all of the ResponseEntity and List results
 * to JSON
 *
 * @author Kai Presler-Marshall
 * @author Michelle Lemons
 * @author Osama Albahrani
 *
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APIInventoryController extends APIController {

    /**
     * InventoryService object, to be autowired in by Spring to allow for
     * manipulating the Inventory model
     */
    @Autowired
    private InventoryService service;

    /**
     * REST API endpoint to provide GET access to the CoffeeMaker's singleton
     * Inventory. This will convert the Inventory to JSON.
     *
     * @return response to the request
     */
    @GetMapping ( BASE_PATH + "/inventory" )
    public ResponseEntity getInventory () {
        final Inventory inventory = service.getInventory();
        return new ResponseEntity( inventory, HttpStatus.OK );
    }

    /**
     * REST API endpoint to provide update access to CoffeeMaker's singleton
     * Inventory. This will update the Inventory of the CoffeeMaker by adding
     * amounts from the Inventory provided to the CoffeeMaker's stored inventory
     *
     * @param inventory
     *            amounts to add to inventory
     * @return response to the request
     */
    @PutMapping ( BASE_PATH + "/inventory" )
    public ResponseEntity updateInventory ( @RequestBody final Inventory inventory ) {
        final Inventory inventoryCurrent = service.getInventory();
        System.out.println( inventoryCurrent.toString() );
        inventoryCurrent.addIngredients( inventory.getIngredients() );
        service.save( inventoryCurrent );
        return new ResponseEntity( inventoryCurrent, HttpStatus.OK );
    }

    /**
     * REST API endpoint to provide add access to CoffeeMaker's singleton
     * Inventory. This will add an Ingredient to the Inventory if it is valid.
     *
     * @param ingredient
     *            ingredient to add
     * @return response to the request
     */
    @PostMapping ( BASE_PATH + "inventory" )
    public ResponseEntity addIngredient ( @RequestBody final Ingredient ingredient ) {
        final Inventory inventoryCurrent = service.getInventory();

        // This is for error-checking
        try {
            ingredient.setName( ingredient.getName() );
            ingredient.setAmount( ingredient.getAmount() );
        }
        catch ( final IllegalArgumentException e ) {
            return new ResponseEntity( inventoryCurrent, HttpStatus.BAD_REQUEST );
        }

        final List<Ingredient> currentIngredients = inventoryCurrent.getIngredients();

        for ( final Ingredient other : currentIngredients ) {
            if ( other.getName().equals( ingredient.getName() ) ) {
                return new ResponseEntity( inventoryCurrent, HttpStatus.CONFLICT );
            }
        }

        inventoryCurrent.addIngredient( ingredient.getName(), ingredient.getAmount() );
        service.save( inventoryCurrent );
        return new ResponseEntity( inventoryCurrent, HttpStatus.OK );
    }
}
