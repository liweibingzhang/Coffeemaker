/**
 *
 */
package edu.ncsu.csc.CoffeeMaker.models;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

/**
 * Tests Inventory.java methods
 *
 * @author Osama Albahrani
 *
 */
class InventoryTest {

    /**
     * Test method for
     * {@link edu.ncsu.csc.CoffeeMaker.models.Inventory#setId(java.lang.Long)}.
     */
    @Test
    void testSetId () {
        final Inventory i = new Inventory();
        i.setId( 1234L );
        assertEquals( 1234L, i.getId() );

        i.setId( -50L );
        assertEquals( -50L, i.getId() );

        i.setId( 0L );
        assertEquals( 0, i.getId() );
    }

    /**
     * Test method for set{Chocolate,Coffee,Milk,Sugar}.
     */
    @Test
    void testSetIngredients () {

        // Create a list of ingredients to add to the inventory
        final ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
        final Ingredient i1 = new Ingredient( "Coffee", 1 );
        final Ingredient i2 = new Ingredient( "Milk", 2 );
        final Ingredient i3 = new Ingredient( "Sugar", 3 );
        final Ingredient i4 = new Ingredient( "Chocolate", 4 );

        ingredients.add( i1 );
        ingredients.add( i2 );
        ingredients.add( i3 );
        ingredients.add( i4 );

        final Inventory i = new Inventory( ingredients );

        // Nothing should change
        assertEquals( "Coffee: 1\n" + "Milk: 2\n" + "Sugar: 3\n" + "Chocolate: 4\n", i.toString() );
    }

    /**
     * Test method for testCheckIngredients
     */
    @Test
    void testCheckIngredients () {
        final Inventory i = new Inventory();
        // Ensure that the proper excpetions are thrown/not thrown.
        assertThrows( IllegalArgumentException.class, () -> i.checkIngredient( "string" ) );
        assertThrows( IllegalArgumentException.class, () -> i.checkIngredient( "-1" ) );
        assertDoesNotThrow( () -> i.checkIngredient( "1" ) );

    }

    /**
     * Test method for
     * {@link edu.ncsu.csc.CoffeeMaker.models.Inventory#enoughIngredients(edu.ncsu.csc.CoffeeMaker.models.Recipe)}.
     */
    @Test
    void testEnoughIngredients () {
        // Create a list of ingredients to add to the inventory
        final ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
        final Ingredient i1 = new Ingredient( "Coffee", 2 );
        final Ingredient i2 = new Ingredient( "Milk", 2 );
        final Ingredient i3 = new Ingredient( "Sugar", 3 );
        final Ingredient i4 = new Ingredient( "Chocolate", 4 );
        ingredients.add( i1 );
        ingredients.add( i2 );
        ingredients.add( i3 );
        ingredients.add( i4 );
        final Inventory i = new Inventory( ingredients );
        // Create a recipe that uses too much of each ingredient
        final Recipe tooMuch = new Recipe( "Too Much", new ArrayList<Ingredient>(), 1 );
        tooMuch.addIngredient( new Ingredient( "Coffee", 10 ) );
        tooMuch.addIngredient( new Ingredient( "Milk", 10 ) );
        tooMuch.addIngredient( new Ingredient( "Sugar", 10 ) );
        tooMuch.addIngredient( new Ingredient( "Chocolate", 10 ) );
        // We expect the inventory not to have enough
        assertFalse( i.enoughIngredients( tooMuch ) );
        // Create a recipe that uses enough of each ingredient
        final Recipe enough = new Recipe( "Enough", new ArrayList<Ingredient>(), 1 );
        enough.addIngredient( new Ingredient( "Coffee", 1 ) );
        enough.addIngredient( new Ingredient( "Milk", 1 ) );
        enough.addIngredient( new Ingredient( "Sugar", 1 ) );
        enough.addIngredient( new Ingredient( "Chocolate", 1 ) );
        // We expect the inventory to have enough
        assertTrue( i.enoughIngredients( enough ) );
    }

    /**
     * Test method for
     * {@link edu.ncsu.csc.CoffeeMaker.models.Inventory#useIngredients(edu.ncsu.csc.CoffeeMaker.models.Recipe)}.
     */
    @Test
    void testUseIngredients () {
        // Create a list of ingredients to add to the inventory
        final ArrayList<Ingredient> inventoryIngredients = new ArrayList<Ingredient>();
        final Ingredient i1 = new Ingredient( "Coffee", 5 );
        final Ingredient i2 = new Ingredient( "Milk", 5 );
        final Ingredient i3 = new Ingredient( "Sugar", 5 );
        final Ingredient i4 = new Ingredient( "Chocolate", 5 );
        inventoryIngredients.add( i1 );
        inventoryIngredients.add( i2 );
        inventoryIngredients.add( i3 );
        inventoryIngredients.add( i4 );
        final Inventory i = new Inventory( inventoryIngredients );

        // Create a recipe that requires too much of an ingredient
        final ArrayList<Ingredient> recipeIngredients = new ArrayList<Ingredient>();
        final Ingredient r1 = new Ingredient( "Coffee", 5 );
        final Ingredient r2 = new Ingredient( "Milk", 5 );
        final Ingredient r3 = new Ingredient( "Sugar", 5 );
        final Ingredient r4 = new Ingredient( "Chocolate", 6 );
        recipeIngredients.add( r1 );
        recipeIngredients.add( r2 );
        recipeIngredients.add( r3 );
        recipeIngredients.add( r4 );

        final Recipe tooMuch = new Recipe( "Too Much", recipeIngredients, 1 );

        assertFalse( i.useIngredients( tooMuch ) );

        final Recipe enough = new Recipe( "Enough", new ArrayList<Ingredient>(), 1 );
        enough.addIngredient( new Ingredient( "Coffee", 1 ) );
        enough.addIngredient( new Ingredient( "Milk", 2 ) );
        enough.addIngredient( new Ingredient( "Sugar", 3 ) );
        enough.addIngredient( new Ingredient( "Chocolate", 4 ) );
        assertTrue( i.useIngredients( enough ) );
    }

    /**
     * Test method for
     * {@link edu.ncsu.csc.CoffeeMaker.models.Inventory#toString()}.
     */
    @Test
    void testToString () {
        final ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
        final Ingredient i1 = new Ingredient( "Coffee", 1 );
        final Ingredient i2 = new Ingredient( "Milk", 2 );
        final Ingredient i3 = new Ingredient( "Sugar", 3 );
        final Ingredient i4 = new Ingredient( "Chocolate", 4 );
        ingredients.add( i1 );
        ingredients.add( i2 );
        ingredients.add( i3 );
        ingredients.add( i4 );
        final Inventory i = new Inventory( ingredients );

        assertEquals( "Coffee: 1\n" + "Milk: 2\n" + "Sugar: 3\n" + "Chocolate: 4\n", i.toString() );
    }

}
