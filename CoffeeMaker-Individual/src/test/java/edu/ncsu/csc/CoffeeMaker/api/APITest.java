package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.CoffeeMaker.common.TestUtils;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;

@ExtendWith ( SpringExtension.class )
@SpringBootTest
@AutoConfigureMockMvc
class APITest {
    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
    }

    /**
     * Testing to get a recipe from an endpoint
     */
    @Test
    @Transactional
    void testGetRecipe () {
        try {
            String recipe = mvc.perform( get( "/api/v1/recipes" ) ).andDo( print() ).andExpect( status().isOk() )
                    .andReturn().getResponse().getContentAsString();

            System.out.println( "xyz:  " );
            System.out.print( recipe );

            /* Figure out if the recipe we want is present */
            if ( !recipe.contains( "mocha" ) ) {
                final Recipe r = new Recipe();

                r.setPrice( 10 );
                r.setName( "mocha" );

                mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( r ) ) ).andExpect( status().isOk() );

            }

            recipe = mvc.perform( get( "/api/v1/recipes" ) ).andDo( print() ).andExpect( status().isOk() ).andReturn()
                    .getResponse().getContentAsString();

            assertTrue( recipe.contains( "mocha" ) );
        }
        catch ( final Exception e ) {
            fail( "Cannot hit the endpoint of /api/v1/recipes" );
        }

    }

    /**
     * Testing to update a inventory
     */
    @Test
    @Transactional
    void testUpdateInventory () {

        // Create a list of ingredients to be used in the inventory
        final ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
        // Create a few ingredients.
        final Ingredient i1 = new Ingredient( "Oat milk", 10 );
        final Ingredient i2 = new Ingredient( "Soy milk", 5 );
        // Add ingredients to list of ingredients
        ingredients.add( i1 );
        ingredients.add( i2 );
        // Construct an inventory with the given list of ingredients.

        final Inventory ivt = new Inventory( ingredients );

        try {
            mvc.perform( put( "/api/v1/inventory" ).contentType( MediaType.APPLICATION_JSON )
                    .content( TestUtils.asJsonString( ivt ) ) ).andExpect( status().isOk() );

            final String inventory = mvc.perform( get( "/api/v1/inventory" ) ).andDo( print() )
                    .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

            assertTrue( inventory.contains( "Oat milk" ) && inventory.contains( "Soy milk" ) );
        }

        catch ( final Exception e ) {
            fail( "Cannot hit the endpoint of /api/v1/inventory" );
        }
    }

    /**
     * Testing to make a coffee
     */
    @Test
    @Transactional
    void testMakeCoffee () {

        try {

            final String recipe = mvc.perform( get( "/api/v1/recipes" ) ).andDo( print() ).andExpect( status().isOk() )
                    .andReturn().getResponse().getContentAsString();

            System.out.print( "herererer:" );
            System.out.print( recipe );

            /* Figure out if the recipe we want is present */
            if ( !recipe.contains( "mocha" ) ) {
                final Recipe r = new Recipe( "mocha", new ArrayList<Ingredient>(), 10 );
                final Ingredient i1 = new Ingredient( "Coffee", 1 );
                final Ingredient i2 = new Ingredient( "Milk", 1 );
                final Ingredient i3 = new Ingredient( "Sugar", 2 );
                final Ingredient i4 = new Ingredient( "Chocolate", 2 );

                r.addIngredient( i1 );
                r.addIngredient( i2 );
                r.addIngredient( i3 );
                r.addIngredient( i4 );
                r.setPrice( 10 );
                r.setName( "mocha" );

                mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( r ) ) ).andExpect( status().isOk() );

            }

            final Ingredient i1 = new Ingredient( "Coffee", 10 );
            final Ingredient i2 = new Ingredient( "Milk", 10 );
            final Ingredient i3 = new Ingredient( "Sugar", 20 );
            final Ingredient i4 = new Ingredient( "Chocolate", 20 );

            // Create a list of ingredients to be used in the inventory
            final ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
            // Add ingredients to list of ingredients
            ingredients.add( i1 );
            ingredients.add( i2 );
            ingredients.add( i3 );
            ingredients.add( i4 );
            // Construct an inventory with the given list of ingredients.

            final Inventory ivt = new Inventory( ingredients );

            mvc.perform( put( "/api/v1/inventory" ).contentType( MediaType.APPLICATION_JSON )
                    .content( TestUtils.asJsonString( ivt ) ) ).andExpect( status().isOk() );

            mvc.perform( post( String.format( "/api/v1/makecoffee/%s", "mocha" ) )
                    .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( 10 ) ) )
                    .andExpect( status().isOk() ).andDo( print() );

        }

        catch ( final Exception e ) {
            fail( "Cannot hit the endpoint of api/v1/makecoffee" );
        }
    }

}
