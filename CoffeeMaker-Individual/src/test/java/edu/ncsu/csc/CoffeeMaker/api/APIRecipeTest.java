package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
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
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.CoffeeMaker.common.TestUtils;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith ( SpringExtension.class )
public class APIRecipeTest {

    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private RecipeService         service;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

        service.deleteAll();
    }

    @Test
    @Transactional
    public void ensureRecipe () throws Exception {
        service.deleteAll();

        final Recipe r = new Recipe( "Mocha", new ArrayList<Ingredient>(), 10 );
        r.addIngredient( new Ingredient( "Chocolate", 10 ) );
        r.addIngredient( new Ingredient( "Milk", 20 ) );
        r.addIngredient( new Ingredient( "Sugar", 5 ) );
        r.addIngredient( new Ingredient( "Coffee", 1 ) );
        r.addIngredient( new Ingredient( "Oat Milk", 1 ) );

        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r ) ) ).andExpect( status().isOk() );

    }

    @Test
    @Transactional
    public void testRecipeAPI () throws Exception {

        service.deleteAll();

        final Recipe r = new Recipe( "Delicious Not-Coffee", new ArrayList<Ingredient>(), 5 );
        r.addIngredient( new Ingredient( "Chocolate", 10 ) );
        r.addIngredient( new Ingredient( "Milk", 20 ) );
        r.addIngredient( new Ingredient( "Sugar", 5 ) );
        r.addIngredient( new Ingredient( "Coffee", 1 ) );

        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r ) ) );

        Assertions.assertEquals( 1, (int) service.count() );

    }

    @Test
    @Transactional
    public void testAddRecipe2 () throws Exception {

        /* Tests a recipe with a duplicate name to make sure it's rejected */

        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        final Recipe r1 = createRecipe( name, 50, 3, 1, 1, 0 );

        service.save( r1 );

        final Recipe r2 = createRecipe( name, 50, 3, 1, 1, 0 );
        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r2 ) ) ).andExpect( status().is4xxClientError() );

        Assertions.assertEquals( 1, service.findAll().size(), "There should only one recipe in the CoffeeMaker" );
    }

    @Test
    @Transactional
    public void testAddRecipe15 () throws Exception {

        /* Tests to make sure that our cap of 3 recipes is enforced */

        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Mocha", 50, 3, 1, 1, 2 );
        service.save( r2 );
        final Recipe r3 = createRecipe( "Latte", 60, 3, 2, 2, 0 );
        service.save( r3 );

        Assertions.assertEquals( 3, service.count(),
                "Creating three recipes should result in three recipes in the database" );

        final Recipe r4 = createRecipe( "Hot Chocolate", 75, 0, 2, 1, 2 );

        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r4 ) ) ).andExpect( status().isInsufficientStorage() );

        Assertions.assertEquals( 3, service.count(), "Creating a fourth recipe should not get saved" );
    }

    @Test
    @Transactional
    public void testDelete () throws Exception {
        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        final Recipe r2 = createRecipe( "Mocha", 70, 2, 2, 2, 0 );

        service.save( r1 );
        service.save( r2 );

        Recipe retrieved = service.findByName( "Coffee" );

        Assertions.assertEquals( 50, (int) retrieved.getPrice() );
        Assertions.assertEquals( 3, retrieved.getIngredient( "Coffee" ).getAmount() );
        Assertions.assertEquals( 1, retrieved.getIngredient( "Milk" ).getAmount() );
        Assertions.assertEquals( 1, retrieved.getIngredient( "Sugar" ).getAmount() );
        Assertions.assertEquals( 0, retrieved.getIngredient( "Chocolate" ).getAmount() );

        retrieved = service.findByName( "Mocha" );
        Assertions.assertEquals( 70, (int) retrieved.getPrice() );
        Assertions.assertEquals( 2, retrieved.getIngredient( "Coffee" ).getAmount() );
        Assertions.assertEquals( 2, retrieved.getIngredient( "Milk" ).getAmount() );
        Assertions.assertEquals( 2, retrieved.getIngredient( "Sugar" ).getAmount() );
        Assertions.assertEquals( 0, retrieved.getIngredient( "Chocolate" ).getAmount() );

        Assertions.assertEquals( 2, service.findAll().size(), "There should only two recipe in the CoffeeMaker" );
        mvc.perform( delete( "/api/v1/recipes/Coffee" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r1 ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 1, service.findAll().size(), "There should one recipe in the CoffeeMaker" );

        retrieved = service.findByName( "Mocha" );
        Assertions.assertEquals( 70, (int) retrieved.getPrice() );
        Assertions.assertEquals( 2, retrieved.getIngredient( "Coffee" ).getAmount() );
        Assertions.assertEquals( 2, retrieved.getIngredient( "Milk" ).getAmount() );
        Assertions.assertEquals( 2, retrieved.getIngredient( "Sugar" ).getAmount() );
        Assertions.assertEquals( 0, retrieved.getIngredient( "Chocolate" ).getAmount() );

        mvc.perform( delete( "/api/v1/recipes/Mocha" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r2 ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 0, service.findAll().size(), "There should zero recipe in the CoffeeMaker" );

        mvc.perform( delete( "/api/v1/recipes/Coffee" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r1 ) ) ).andExpect( status().isNotFound() );

        assertNull( service.findByName( "Mocha" ) );
    }

    @Test
    @Transactional
    public void testUpdate () throws Exception {
        final Recipe r = createRecipe( "Coffee", 20, 16, 17, 18, 19 );
        mvc.perform( put( "/api/v1/recipes/" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r ) ) ).andExpect( status().isNotFound() );
        service.save( r );
        r.addIngredient( new Ingredient( "Eggs", 30 ) );
        mvc.perform( put( "/api/v1/recipes/" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r ) ) ).andExpect( status().isOk() );
        final Recipe retrieved = service.findByName( "Coffee" );
        Assertions.assertEquals( 20, (int) retrieved.getPrice() );
        Assertions.assertEquals( 16, retrieved.getIngredient( "Coffee" ).getAmount() );
        Assertions.assertEquals( 17, retrieved.getIngredient( "Milk" ).getAmount() );
        Assertions.assertEquals( 18, retrieved.getIngredient( "Sugar" ).getAmount() );
        Assertions.assertEquals( 19, retrieved.getIngredient( "Chocolate" ).getAmount() );
        Assertions.assertEquals( 30, retrieved.getIngredient( "Eggs" ).getAmount() );
    }

    private Recipe createRecipe ( final String name, final Integer price, final Integer coffee, final Integer milk,
            final Integer sugar, final Integer chocolate ) {
        final Recipe recipe = new Recipe( name, new ArrayList<Ingredient>(), price );
        recipe.setName( name );
        recipe.setPrice( price );
        recipe.addIngredient( new Ingredient( "Coffee", coffee ) );
        recipe.addIngredient( new Ingredient( "Milk", milk ) );
        recipe.addIngredient( new Ingredient( "Sugar", sugar ) );
        recipe.addIngredient( new Ingredient( "Chocolate", chocolate ) );

        return recipe;
    }

}
