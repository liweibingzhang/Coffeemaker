package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import javax.transaction.Transactional;

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
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith ( SpringExtension.class )
class APIIngredientTest {

    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private IngredientService     service;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

        service.deleteAll();
    }

    /**
     * Testing GET request for the Ingredient API
     *
     * @throws Exception
     */
    @Test
    @Transactional
    void testGetIngredient () throws Exception {
        // mvc.perform( post( "/api/v1/recipes" ).contentType(
        // MediaType.APPLICATION_JSON )
        // .content( TestUtils.asJsonString( r ) ) ).andExpect( status().isOk()
        // );
        mvc.perform( get( "/api/v1/ingredient/milk" ) ).andDo( print() ).andExpect( status().isNotFound() );
        Ingredient newFood = new Ingredient( "Milk", 0 );
        service.save( newFood );
        mvc.perform( get( "/api/v1/ingredient/milk" ) ).andDo( print() ).andExpect( status().isOk() );
        newFood = new Ingredient( "Milk 2", 0 );
        service.save( newFood );
        mvc.perform( get( "/api/v1/ingredients" ) ).andDo( print() ).andExpect( status().isOk() );
    }

    /**
     * Testing the POST request
     *
     */
    @Test
    @Transactional
    void testPostIngredient () throws Exception {
        final Ingredient newFood = new Ingredient( "Milk", 10 );
        mvc.perform( post( "/api/v1/ingredient" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( newFood ) ) ).andExpect( status().isOk() );

        final Ingredient newFood2 = new Ingredient( "Milk2", 11 );
        mvc.perform( post( "/api/v1/ingredient" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( newFood2 ) ) ).andExpect( status().isOk() );

        final List<Ingredient> ingredients = service.findAll();
        assertEquals( "Milk", ingredients.get( 0 ).getName() );
        assertEquals( 10, ingredients.get( 0 ).getAmount() );
        assertEquals( "Milk2", ingredients.get( 1 ).getName() );
        assertEquals( 11, ingredients.get( 1 ).getAmount() );

        mvc.perform( post( "/api/v1/ingredient" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( newFood2 ) ) ).andExpect( status().isConflict() );
    }

    /**
     * Test the Delete Request
     */
    @Test
    @Transactional
    void testDeleteIngredient () throws Exception {
        final Ingredient newFood = new Ingredient( "milk", 10 );
        mvc.perform( delete( "/api/v1/ingredient/milk" ) ).andDo( print() ).andExpect( status().isConflict() );
        service.save( newFood );
        mvc.perform( delete( "/api/v1/ingredient/milk" ) ).andDo( print() ).andExpect( status().isOk() );
        assertEquals( 0, service.count() );
    }

    /**
     * Test the PUT request
     */
    @Test
    @Transactional
    void testPutngrednt () throws Exception {
        final Ingredient newFood = new Ingredient( "milk", 10 );
        final Ingredient newFood2 = new Ingredient( "milk2", 11 );
        final Ingredient newFood3 = new Ingredient( "milk", 13 );
        service.save( newFood );
        mvc.perform( put( "/api/v1/ingredient" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( newFood2 ) ) ).andExpect( status().isConflict() );
        mvc.perform( put( "/api/v1/ingredient" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( newFood3 ) ) ).andExpect( status().isOk() );
        assertEquals( 1, service.count() );
        final List<Ingredient> ingredients = service.findAll();
        assertEquals( "milk", ingredients.get( 0 ).getName() );
        assertEquals( 13, ingredients.get( 0 ).getAmount() );
    }
}
