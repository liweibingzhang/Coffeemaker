package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.CoffeeMaker.common.TestUtils;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;

@ExtendWith ( SpringExtension.class )
@SpringBootTest
@AutoConfigureMockMvc
class APIInventoryTest {
    /** Base path of API */
    static final private String   BASE_PATH = "/api/v1/";

    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    /**
     * InventoryService object, to be autowired in by Spring to allow for
     * manipulating the Inventory model
     */
    @Autowired
    private InventoryService      service;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
    }

    /**
     * Testing adding an ingredient to inventory
     */
    @Test
    @Transactional
    void testAddIngredient () {
        // Test invalid name
        final Ingredient nullName = new Ingredient( null, 5, true );
        final Ingredient emptyName = new Ingredient( "", 13, true );

        assertEquals( 0, service.getInventory().getIngredients().size() );
        try {
            mvc.perform( post( BASE_PATH + "inventory" ).contentType( MediaType.APPLICATION_JSON )
                    .content( TestUtils.asJsonString( nullName ) ) ).andExpect( status().isBadRequest() );
        }
        catch ( final Exception e ) {
            // Do nothing
        }

        assertEquals( 0, service.getInventory().getIngredients().size() );
        try {
            mvc.perform( post( BASE_PATH + "inventory" ).contentType( MediaType.APPLICATION_JSON )
                    .content( TestUtils.asJsonString( emptyName ) ) ).andExpect( status().isBadRequest() );
        }
        catch ( final Exception e ) {
            // Do nothing
        }

        assertEquals( 0, service.getInventory().getIngredients().size() );

        // Test invalid amount
        final Ingredient nullAmount = new Ingredient( "Match", null, true );
        final Ingredient negativeAmount = new Ingredient( "", -1, true );

        assertEquals( 0, service.getInventory().getIngredients().size() );
        try {
            mvc.perform( post( BASE_PATH + "inventory" ).contentType( MediaType.APPLICATION_JSON )
                    .content( TestUtils.asJsonString( nullAmount ) ) ).andExpect( status().isBadRequest() );
        }
        catch ( final Exception e ) {
            // The mvc call could fail due to NPE
        }

        assertEquals( 0, service.getInventory().getIngredients().size() );
        try {
            mvc.perform( post( BASE_PATH + "inventory" ).contentType( MediaType.APPLICATION_JSON )
                    .content( TestUtils.asJsonString( negativeAmount ) ) ).andExpect( status().isBadRequest() );
        }
        catch ( final Exception e ) {
            fail( "Adding " + negativeAmount + " did not throw " + HttpStatus.BAD_REQUEST );
        }

        assertEquals( 0, service.getInventory().getIngredients().size() );

        final Ingredient honeyFive = new Ingredient( "Honey", 5 );
        final Ingredient caramelTwentyOne = new Ingredient( "Caramel", 21 );

        assertEquals( 0, service.getInventory().getIngredients().size() );
        try {
            mvc.perform( post( BASE_PATH + "inventory" ).contentType( MediaType.APPLICATION_JSON )
                    .content( TestUtils.asJsonString( honeyFive ) ) ).andExpect( status().isOk() );
        }
        catch ( final Exception e ) {
            fail( "Unable to add " + honeyFive );
        }

        assertEquals( 1, service.getInventory().getIngredients().size() );
        try {
            mvc.perform( post( BASE_PATH + "inventory" ).contentType( MediaType.APPLICATION_JSON )
                    .content( TestUtils.asJsonString( caramelTwentyOne ) ) ).andExpect( status().isOk() );
        }
        catch ( final Exception e ) {
            fail( "Unable to add " + caramelTwentyOne );
        }

        assertEquals( 2, service.getInventory().getIngredients().size() );

        final List<Ingredient> ingredients = service.getInventory().getIngredients();
        assertEquals( honeyFive, ingredients.get( 0 ) );
        assertEquals( caramelTwentyOne, ingredients.get( 1 ) );

        // Try to add another Honey
        final Ingredient otherHoney = new Ingredient( "Honey", 3 );

        try {
            mvc.perform( post( BASE_PATH + "inventory" ).contentType( MediaType.APPLICATION_JSON )
                    .content( TestUtils.asJsonString( otherHoney ) ) ).andExpect( status().isConflict() );
        }
        catch ( final Exception e ) {
            // Intentionally empty
        }
        assertEquals( 2, service.getInventory().getIngredients().size() );

        // Try to add the same caramel again
        try {
            mvc.perform( post( BASE_PATH + "inventory" ).contentType( MediaType.APPLICATION_JSON )
                    .content( TestUtils.asJsonString( caramelTwentyOne ) ) ).andExpect( status().isConflict() );
        }
        catch ( final Exception e ) {
            // Intentionally empty
        }
        assertEquals( 2, service.getInventory().getIngredients().size() );
    }
}
