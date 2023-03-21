package edu.ncsu.csc.CoffeeMaker.unit;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.CoffeeMaker.TestConfig;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class RecipeTest {

    @Autowired
    private RecipeService service;

    @BeforeEach
    public void setup () {
        service.deleteAll();
    }

    @Test
    @Transactional
    public void testAddRecipe () {
        final Recipe r1 = createRecipe( "Black Coffee", 1, 1, 0, 0, 0 );
        service.save( r1 );

        final Recipe r2 = createRecipe( "Mocha", 1, 1, 1, 1, 1 );
        service.save( r2 );

        final List<Recipe> recipes = service.findAll();
        Assertions.assertEquals( 2, recipes.size(),
                "Creating two recipes should result in two recipes in the database" );

        Assertions.assertEquals( r1, recipes.get( 0 ), "The retrieved recipe should match the created one" );
    }

    @Test
    @Transactional
    public void testNoRecipes () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Tasty Drink", 12, -12, 0, 0, 0 );

        final Recipe r2 = createRecipe( "Mocha", 1, 1, 1, 1, 1 );

        final List<Recipe> recipes = List.of( r1, r2 );

        try {
            service.saveAll( recipes );
            Assertions.assertEquals( 0, service.count(),
                    "Trying to save a collection of elements where one is invalid should result in neither getting saved" );
        }
        catch ( final Exception e ) {
            Assertions.assertTrue( e instanceof ConstraintViolationException );
        }

    }

    @Test
    @Transactional
    public void testAddRecipe1 () {

        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        final Recipe r1 = createRecipe( name, 50, 3, 1, 1, 0 );

        service.save( r1 );

        Assertions.assertEquals( 1, service.findAll().size(), "There should only one recipe in the CoffeeMaker" );
        Assertions.assertNotNull( service.findByName( name ) );

    }

    /* Test2 is done via the API for different validation */

    @Test
    @Transactional
    public void testAddRecipe3 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        final Recipe r1 = createRecipe( name, -50, 3, 1, 1, 0 );

        try {
            service.save( r1 );

            Assertions.assertNull( service.findByName( name ),
                    "A recipe was able to be created with a negative price" );
        }
        catch ( final ConstraintViolationException cvee ) {
            // expected
        }

    }

    @Test
    @Transactional
    public void testAddRecipe4 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        final Recipe r1 = createRecipe( name, 50, -3, 1, 1, 2 );

        try {
            service.save( r1 );

            Assertions.assertNull( service.findByName( name ),
                    "A recipe was able to be created with a negative amount of coffee" );
        }
        catch ( final ConstraintViolationException cvee ) {
            // expected
        }

    }

    @Test
    @Transactional
    public void testAddRecipe5 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        final Recipe r1 = createRecipe( name, 50, 3, -1, 1, 2 );

        try {
            service.save( r1 );

            Assertions.assertNull( service.findByName( name ),
                    "A recipe was able to be created with a negative amount of milk" );
        }
        catch ( final ConstraintViolationException cvee ) {
            // expected
        }

    }

    @Test
    @Transactional
    public void testAddRecipe6 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        final Recipe r1 = createRecipe( name, 50, 3, 1, -1, 2 );

        try {
            service.save( r1 );

            Assertions.assertNull( service.findByName( name ),
                    "A recipe was able to be created with a negative amount of sugar" );
        }
        catch ( final ConstraintViolationException cvee ) {
            // expected
        }

    }

    @Test
    @Transactional
    public void testAddRecipe7 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        final Recipe r1 = createRecipe( name, 50, 3, 1, 1, -2 );

        try {
            service.save( r1 );

            Assertions.assertNull( service.findByName( name ),
                    "A recipe was able to be created with a negative amount of chocolate" );
        }
        catch ( final ConstraintViolationException cvee ) {
            // expected
        }

    }

    @Test
    @Transactional
    public void testAddRecipe13 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Mocha", 50, 3, 1, 1, 2 );
        service.save( r2 );

        Assertions.assertEquals( 2, service.count(),
                "Creating two recipes should result in two recipes in the database" );

    }

    @Test
    @Transactional
    public void testAddRecipe14 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Mocha", 50, 3, 1, 1, 2 );
        service.save( r2 );
        final Recipe r3 = createRecipe( "Latte", 60, 3, 2, 2, 0 );
        service.save( r3 );

        Assertions.assertEquals( 3, service.count(),
                "Creating three recipes should result in three recipes in the database" );

    }

    @Test
    @Transactional
    public void testDeleteRecipe1 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        service.save( r1 );

        Assertions.assertEquals( 1, service.count(), "There should be one recipe in the database" );

        service.delete( r1 );
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
    }

    @Test
    @Transactional
    public void testDeleteRecipe2 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Mocha", 50, 3, 1, 1, 2 );
        service.save( r2 );
        final Recipe r3 = createRecipe( "Latte", 60, 3, 2, 2, 0 );
        service.save( r3 );

        Assertions.assertEquals( 3, service.count(), "There should be three recipes in the database" );

        service.deleteAll();

        Assertions.assertEquals( 0, service.count(), "`service.deleteAll()` should remove everything" );

    }

    @Test
    @Transactional
    public void testDeleteRecipe3 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Mocha", 50, 3, 1, 1, 2 );
        service.save( r2 );
        final Recipe r3 = createRecipe( "Latte", 60, 3, 2, 2, 0 );
        service.save( r3 );

        Assertions.assertEquals( 3, service.count(), "There should be three recipes in the database" );

        service.delete( r2 );

        try {

            Assertions.assertNull( service.findByName( "Mocha" ), "A recipe that was deleted was found" );
            Assertions.assertNull( service.findById( r2.getId() ), "A recipe that was deleted was found" );
        }
        catch ( final ConstraintViolationException cvee ) {
            // expected
        }

        Assertions.assertEquals( 2, service.count(), "`service.delete()` should remove Coffee" );

        final Recipe dbCoffee = service.findByName( "Coffee" );
        assertNotNull( dbCoffee );
        Assertions.assertEquals( r1, dbCoffee );

        final Recipe dbLatte = service.findByName( "Latte" );
        assertNotNull( dbLatte );
        Assertions.assertEquals( r3, dbLatte );

    }

    @Test
    @Transactional
    public void testDeleteRecipe4 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Mocha", 50, 3, 1, 1, 2 );
        service.save( r2 );
        final Recipe r3 = createRecipe( "Latte", 60, 3, 2, 2, 0 );

        Assertions.assertEquals( 2, service.count(), "There should be two recipes in the database" );

        // Try deleting a recipe that is not in the database
        service.delete( r3 );

        Assertions.assertEquals( 2, service.count(), "There should be two recipes in the database" );

    }

    @Test
    @Transactional
    public void testDeleteRecipe5 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Mocha", 50, 3, 1, 1, 2 );
        service.save( r2 );
        final Recipe r3 = createRecipe( "Latte", 60, 3, 2, 2, 0 );
        service.save( r3 );
        final Recipe r4 = createRecipe( "Cappuccino", 60, 3, 2, 2, 0 );
        service.save( r4 );

        Assertions.assertEquals( 4, service.count(), "There should be four recipes in the database" );

        service.delete( r1 );
        service.delete( r2 );
        service.delete( r3 );

        Assertions.assertEquals( 1, service.count(), "There should be one recipes in the database" );

        final Recipe dbCappuccino = service.findByName( "Cappuccino" );
        assertNotNull( dbCappuccino );
        Assertions.assertEquals( r4, dbCappuccino );

        final Recipe dbCoffee = service.findByName( "Coffee" );
        Assertions.assertNull( dbCoffee );

        final Recipe dbMocha = service.findByName( "Mocha" );
        Assertions.assertNull( dbMocha );

        final Recipe dbLatte = service.findByName( "Latte" );
        Assertions.assertNull( dbLatte );

    }

    @Test
    @Transactional
    public void testEditRecipe1 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        service.save( r1 );

        r1.setPrice( 70 );

        service.save( r1 );

        final Recipe retrieved = service.findByName( "Coffee" );

        Assertions.assertEquals( 70, (int) retrieved.getPrice() );
        Assertions.assertEquals( 3, (int) retrieved.getIngredient( "Coffee" ).getAmount() );
        Assertions.assertEquals( 1, (int) retrieved.getIngredient( "Milk" ).getAmount() );
        Assertions.assertEquals( 1, (int) retrieved.getIngredient( "Sugar" ).getAmount() );
        Assertions.assertEquals( 0, (int) retrieved.getIngredient( "Chocolate" ).getAmount() );

        Assertions.assertEquals( 1, service.count(), "Editing a recipe shouldn't duplicate it" );

    }

    @Test
    public void testCheckRecipe () {
        final Recipe r1 = createRecipe( "Coffee", 50, 0, 0, 0, 0 );
        assertTrue( r1.checkRecipe() );
        final Recipe r2 = createRecipe( "Coffee", 50, 1, 0, 0, 0 );
        assertFalse( r2.checkRecipe() );
    }

    @Test
    public void testUpdateRecipe () {
        final Recipe r1 = createRecipe( "Coffee", 50, 0, 0, 0, 0 );
        final Recipe r2 = createRecipe( "Coffee", 50, 10, 20, 30, 40 );
        r1.updateRecipe( r2 );

        assertTrue( r1.getName().equals( r2.getName() ) );
        Assertions.assertEquals( 50, (int) r1.getPrice() );
        Assertions.assertEquals( 10, (int) r1.getIngredient( "Coffee" ).getAmount() );
        Assertions.assertEquals( 20, (int) r1.getIngredient( "Milk" ).getAmount() );
        Assertions.assertEquals( 30, (int) r1.getIngredient( "Sugar" ).getAmount() );
        Assertions.assertEquals( 40, (int) r1.getIngredient( "Chocolate" ).getAmount() );

        // Try updating an ingredient with null
        try {
            r1.removeIngredient( null );
            fail( "Was able to update with null parameter." );
        }
        catch ( final Exception e ) {

            assertEquals( IllegalArgumentException.class, e.getClass() );

        }
    }

    @Test
    public void testEquals () {
        final Recipe r1 = createRecipe( "Coffee", 50, 0, 0, 0, 0 );
        final Recipe r2 = createRecipe( "Coffee", 50, 0, 0, 0, 0 );
        final Recipe r3 = createRecipe( "Coffee 2", 50, 0, 0, 0, 0 );
        assertTrue( r1.equals( r2 ) );
        assertFalse( r1.equals( r3 ) );
    }

    @Test
    @Transactional
    public void testFindById () {
        // Create 3 distinct Recipes
        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Mocha", 50, 3, 1, 1, 2 );
        service.save( r2 );
        final Recipe r3 = createRecipe( "Latte", 60, 3, 2, 2, 0 );
        service.save( r3 );

        // Test null id
        Assertions.assertNull( service.findById( null ) );

        // Test valid id
        Assertions.assertEquals( r1, service.findById( r1.getId() ) );

        // Test invalid id with unsaved recipe
        final Recipe r4 = createRecipe( "Coffee 2", 50, 0, 0, 0, 0 );
        Assertions.assertNull( service.findById( r4.getId() ) );

    }

    @Test
    @Transactional
    public void testExistsById () {
        // Create 3 distinct Recipes
        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Mocha", 50, 3, 1, 1, 2 );
        service.save( r2 );
        final Recipe r3 = createRecipe( "Latte", 60, 3, 2, 2, 0 );

        // Test that r1 exists
        Assertions.assertTrue( service.existsById( r1.getId() ) );

        // Expect exception for null passed in
        try {
            service.existsById( null );
            fail( "existsByID should throw an exception when null is passed" );
        }
        catch ( final Exception e ) {
            Assertions.assertEquals( InvalidDataAccessApiUsageException.class, e.getClass() );

        }

    }

    @Test
    public void testRemoveIngredient () {
        final Recipe r1 = new Recipe( "Mocha", new ArrayList<Ingredient>(), 1 );
        // Create three ingredients
        final Ingredient i1 = new Ingredient( "Coffee", 1 );
        final Ingredient i2 = new Ingredient( "Milk", 2 );
        final Ingredient i3 = new Ingredient( "Oat Milk", 3 );
        final Ingredient i4 = new Ingredient( "Vanilla", 1 );

        r1.addIngredient( i1 );
        r1.addIngredient( i2 );
        r1.addIngredient( i3 );

        // Try to remove milk
        assertTrue( r1.removeIngredient( i2 ) );

        // Ensure there are only the correct ingredients
        assertTrue( r1.getIngredients().contains( i1 ) );
        assertFalse( r1.getIngredients().contains( i2 ) );
        assertTrue( r1.getIngredients().contains( i3 ) );

        // Try removing an ingredient thats not in the recipe
        try {
            r1.removeIngredient( i4 );
            fail( "Was able to remove an ingredient that was not in the recipe." );
        }
        catch ( final Exception e ) {

            assertEquals( IllegalArgumentException.class, e.getClass() );

        }

        // Try removing an ingredient thats null
        try {
            r1.removeIngredient( null );
            fail( "Was able to remove an ingredient that was not in the recipe." );
        }
        catch ( final Exception e ) {

            assertEquals( IllegalArgumentException.class, e.getClass() );

        }

    }

    @Test
    public void testSetIngredientAmount () {
        final Recipe r1 = new Recipe( "Mocha", new ArrayList<Ingredient>(), 1 );
        // Create three ingredients
        final Ingredient i1 = new Ingredient( "Coffee", 1 );
        final Ingredient i2 = new Ingredient( "Milk", 2 );
        final Ingredient i3 = new Ingredient( "Oat Milk", 3 );

        r1.addIngredient( i1 );
        r1.addIngredient( i2 );
        r1.addIngredient( i3 );

        // Try to edit amount of Oat Milk
        r1.setIngredientAmount( "Oat Milk", 32 );

        assertEquals( 1, r1.getIngredient( "Coffee" ).getAmount() );
        assertEquals( 2, r1.getIngredient( "Milk" ).getAmount() );
        assertEquals( 32, r1.getIngredient( "Oat Milk" ).getAmount() );

    }

    // Helper method to create a recipe with only primary ingredients
    private Recipe createRecipe ( final String name, final Integer price, final Integer coffeeNum,
            final Integer milkNum, final Integer sugarNum, final Integer chocolateNum ) {
        final Recipe recipe = new Recipe( name, new ArrayList<Ingredient>(), price );
        final Ingredient coffee = new Ingredient( "Coffee", coffeeNum );
        final Ingredient milk = new Ingredient( "Milk", milkNum );
        final Ingredient sugar = new Ingredient( "Sugar", sugarNum );
        final Ingredient chocolate = new Ingredient( "Chocolate", chocolateNum );

        recipe.setName( name );
        recipe.setPrice( price );

        recipe.addIngredient( coffee );
        recipe.addIngredient( milk );
        recipe.addIngredient( sugar );
        recipe.addIngredient( chocolate );

        return recipe;
    }

}
