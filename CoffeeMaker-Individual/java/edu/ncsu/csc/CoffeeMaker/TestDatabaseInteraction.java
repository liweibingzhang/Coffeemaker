/**
 *
 */
package edu.ncsu.csc.CoffeeMaker;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )

/**
 * Tests sending and receiving data from the database
 *
 * @author Osama Albahrani (osalbahr)
 * @author Mathew Chanda
 *
 */
public class TestDatabaseInteraction {
    @Autowired
    private RecipeService recipeService;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        recipeService.deleteAll();
    }

    /**
     * Tests the RecipeService class
     */
    @Test
    @Transactional
    public void testRecipes () {
        // Test adding one recipe
        final Recipe rMocha = new Recipe( "Mocha", new ArrayList<Ingredient>(), 350 );

        rMocha.setName( "Mocha" );
        rMocha.setPrice( 350 );
        rMocha.addIngredient( new Ingredient( "Coffee", 2 ) );
        rMocha.addIngredient( new Ingredient( "Sugar", 1 ) );
        rMocha.addIngredient( new Ingredient( "Milk", 1 ) );
        rMocha.addIngredient( new Ingredient( "Chocolate", 1 ) );

        recipeService.save( rMocha );

        final List<Recipe> dbRecipes = recipeService.findAll();

        assertEquals( 1, dbRecipes.size() );

        final Recipe dbRecipeMocha = dbRecipes.get( 0 );

        myAssertEquals( "Equal Mocha using get(0)", rMocha, dbRecipeMocha );

        final Recipe dbRecipeMochaByName = recipeService.findByName( "Mocha" );
        myAssertEquals( "Equal Mocha by name", rMocha, dbRecipeMochaByName );

        // Test adding two more
        final Recipe rChocolate = new Recipe( "Chocolate", new ArrayList<Ingredient>(), 1000 );

        rChocolate.addIngredient( new Ingredient( "Coffee", 0 ) );
        rChocolate.addIngredient( new Ingredient( "Sugar", 5 ) );
        rChocolate.addIngredient( new Ingredient( "Oat Milk", 10 ) );
        rChocolate.addIngredient( new Ingredient( "Chocolate", 20 ) );

        final Recipe rDarkChocolate = new Recipe( "Dark Chocolate", new ArrayList<Ingredient>(), 2000 );

        rDarkChocolate.addIngredient( new Ingredient( "Coffee", 0 ) );
        rDarkChocolate.addIngredient( new Ingredient( "Sugar", 0 ) );
        rDarkChocolate.addIngredient( new Ingredient( "Oat Milk", 0 ) );
        rDarkChocolate.addIngredient( new Ingredient( "Chocolate", 50 ) );

        recipeService.save( rChocolate );
        recipeService.save( rDarkChocolate );

        final Recipe dbChocolate = recipeService.findByName( "Chocolate" );
        assertNotNull( dbChocolate );
        final Recipe dbDarkChocolate = recipeService.findByName( "Dark Chocolate" );
        assertNotNull( dbDarkChocolate );
        myAssertEquals( "Equal Chocolate", rChocolate, dbChocolate );
        myAssertEquals( "Equal Dark Chocolate", rDarkChocolate, dbDarkChocolate );

        // Test non-existent and null names
        assertNull( recipeService.findByName( "Latte" ) );
        assertNull( recipeService.findByName( null ) );

        // Test that Mocha still exists
        final Recipe dbRecipeMochaByNameAgain = recipeService.findByName( "Mocha" );
        myAssertEquals( "Equal Mocha using get(0)", rMocha, dbRecipeMochaByNameAgain );
    }

    /**
     * Test if we can the edit the recipe
     */
    @Test
    @Transactional
    public void testEditRecipe () {
        final Recipe rMocha = new Recipe( "Mocha", new ArrayList<Ingredient>(), 350 );

        rMocha.addIngredient( new Ingredient( "Coffee", 2 ) );
        rMocha.addIngredient( new Ingredient( "Sugar", 1 ) );
        rMocha.addIngredient( new Ingredient( "Milk", 1 ) );
        rMocha.addIngredient( new Ingredient( "Chocolate", 1 ) );

        recipeService.save( rMocha );

        List<Recipe> dbRecipes = recipeService.findAll();
        Recipe dbRecipeMocha = dbRecipes.get( 0 );

        myAssertEquals( "Equal Mocha using get(0)", rMocha, dbRecipeMocha );
        Recipe dbRecipeMochaByName = recipeService.findByName( "Mocha" );
        myAssertEquals( "Equal Mocha by name", rMocha, dbRecipeMochaByName );

        // editing the rMocha here
        rMocha.setName( "Mocha 2" );
        rMocha.setPrice( 400 );
        rMocha.setIngredientAmount( "Coffee", 3 );
        rMocha.setIngredientAmount( "Sugar", 4 );
        rMocha.setIngredientAmount( "Milk", 4 );
        rMocha.setIngredientAmount( "Chocolate", 4 );

        recipeService.save( rMocha );

        dbRecipes = recipeService.findAll();
        dbRecipeMocha = dbRecipes.get( 0 );

        myAssertEquals( "Equal Mocha using get(0)", rMocha, dbRecipeMocha );
        dbRecipeMochaByName = recipeService.findByName( "Mocha 2" );
        myAssertEquals( "Equal Mocha by name", rMocha, dbRecipeMochaByName );
    }

    /**
     * Asserts that the recipe and its database counterpart are equal
     *
     * @param message
     *            error message
     * @param r
     *            original recipe (expected)
     * @param dbr
     *            database recipe (actual)
     */
    private void myAssertEquals ( final String message, final Recipe r, final Recipe dbr ) {
        assertAll( message, () -> assertEquals( r.getName(), dbr.getName() ),
                () -> assertEquals( r.getPrice(), dbr.getPrice() ),
                () -> assertEquals( r.getIngredients().toString(), dbr.getIngredients().toString() )

        );
    }

    // private Recipe createRecipe ( final String name, final Integer price,
    // final Integer coffeeNum,
    // final Integer milkNum, final Integer sugarNum, final Integer chocolateNum
    // ) {
    // final Recipe recipe = new Recipe( name, new ArrayList<Ingredient>(),
    // price );
    // final Ingredient coffee = new Ingredient( "Coffee", coffeeNum );
    // final Ingredient milk = new Ingredient( "Milk", milkNum );
    // final Ingredient sugar = new Ingredient( "Sugar", sugarNum );
    // final Ingredient chocolate = new Ingredient( "Chocolate", chocolateNum );
    //
    // recipe.setName( name );
    // recipe.setPrice( price );
    //
    // recipe.addIngredient( coffee );
    // recipe.addIngredient( milk );
    // recipe.addIngredient( sugar );
    // recipe.addIngredient( chocolate );
    //
    // return recipe;
    // }
}
