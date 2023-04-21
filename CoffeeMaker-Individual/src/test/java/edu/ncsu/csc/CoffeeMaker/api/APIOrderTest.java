package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.Cookie;
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
import org.springframework.test.web.servlet.MvcResult;

import edu.ncsu.csc.CoffeeMaker.common.TestUtils;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;
import edu.ncsu.csc.CoffeeMaker.services.OrderQueueService;
import edu.ncsu.csc.CoffeeMaker.services.OrderService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

@ExtendWith ( SpringExtension.class )
@SpringBootTest
@AutoConfigureMockMvc
public class APIOrderTest {

    @Autowired
    private MockMvc           mvc;

    @Autowired
    private OrderService      oService;

    @Autowired
    private OrderQueueService qService;

    @Autowired
    private RecipeService     rService;

    @Autowired
    private InventoryService  iService;

    @Autowired
    private IngredientService ingService;

    Ingredient                ingredientA;
    Ingredient                ingredientB;
    Ingredient                ingredientC;
    Recipe                    recipe;
    Recipe                    recipe2;

    Map<String, String>       customerDetails;

    Map<String, String>       customer2Details;

    Map<String, String>       staffDetails;

    /**
     * Sets up the tests.
     *
     * @throws Exception
     */
    @BeforeEach
    public void setup () throws Exception {
        ingService.deleteAll();
        rService.deleteAll();
        ingredientA = new Ingredient( "A", 2 );
        ingredientB = new Ingredient( "B", 2 );
        ingredientC = new Ingredient( "C", 2 );
        iService.getInventory().addIngredient( "A", 10 );
        iService.getInventory().addIngredient( "B", 10 );
        iService.getInventory().addIngredient( "C", 10 );
        recipe = new Recipe( "R", new ArrayList<Ingredient>(), 5 );
        recipe2 = new Recipe( "R2", new ArrayList<Ingredient>(), 10 );
        recipe.addIngredient( ingredientA );
        recipe.addIngredient( ingredientB );
        recipe2.addIngredient( ingredientC );
        rService.save( recipe );
        rService.save( recipe2 );

        customerDetails = new TreeMap<String, String>();

        customerDetails.put( "username", "customer" );
        customerDetails.put( "password", "pass" );
        customerDetails.put( "type", "Customer" );

        final MvcResult result = mvc
                .perform( post( "/api/v1/user" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( customerDetails ) ) )
                .andExpect( status().isOk() ).andReturn();

        customerDetails.put( "sessionid", result.getResponse().getCookie( "sessionid" ).getValue() );

        staffDetails = new TreeMap<String, String>();

        staffDetails.put( "username", "staff" );
        staffDetails.put( "password", "pass" );
        staffDetails.put( "type", "Staff" );

        final MvcResult result2 = mvc.perform( post( "/api/v1/user" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( staffDetails ) ) ).andExpect( status().isOk() ).andReturn();

        staffDetails.put( "sessionid", result2.getResponse().getCookie( "sessionid" ).getValue() );

        customer2Details = new TreeMap<String, String>();

        customer2Details.put( "username", "customer2" );
        customer2Details.put( "password", "pass" );
        customer2Details.put( "type", "Customer" );

        final MvcResult result3 = mvc
                .perform( post( "/api/v1/user" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( customer2Details ) ) )
                .andExpect( status().isOk() ).andReturn();

        customer2Details.put( "sessionid", result3.getResponse().getCookie( "sessionid" ).getValue() );
    }

    @Test
    @Transactional
    public void testCreateOrder () throws Exception {
        final Map<String, Integer> orderContents = new HashMap<String, Integer>();
        orderContents.put( recipe.getName(), 1 );
        orderContents.put( recipe2.getName(), 2 );

        mvc.perform( put( "/api/v1/order" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( orderContents ) )
                .cookie( new Cookie( "sessionid", customerDetails.get( "sessionid" ) ),
                        new Cookie( "username", customerDetails.get( "username" ) ) ) )
                .andExpect( status().isOk() );
        assertEquals( 1, qService.getOrderQueue().getOrders().size() );

        assertEquals( "customer", qService.getOrderQueue().getOrders().get( 0 ).getUsername() );
    }

    @Test
    @Transactional
    public void testGetOrders () throws Exception {
        final Map<String, Integer> orderContents = new HashMap<String, Integer>();
        orderContents.put( recipe.getName(), 1 );
        orderContents.put( recipe2.getName(), 2 );

        mvc.perform( put( "/api/v1/order" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( orderContents ) )
                .cookie( new Cookie( "sessionid", customerDetails.get( "sessionid" ) ),
                        new Cookie( "username", customerDetails.get( "username" ) ) ) )
                .andExpect( status().isOk() );

        final Map<String, Integer> order2Contents = new HashMap<String, Integer>();
        order2Contents.put( recipe.getName(), 0 );
        order2Contents.put( recipe2.getName(), 1 );

        mvc.perform( put( "/api/v1/order" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( orderContents ) )
                .cookie( new Cookie( "sessionid", customer2Details.get( "sessionid" ) ),
                        new Cookie( "username", customer2Details.get( "username" ) ) ) )
                .andExpect( status().isOk() );

        final MvcResult staffResult = mvc
                .perform( get( "/api/v1/order" ).contentType( MediaType.APPLICATION_JSON ).cookie(
                        new Cookie( "sessionid", staffDetails.get( "sessionid" ) ),
                        new Cookie( "username", staffDetails.get( "username" ) ) ) )
                .andExpect( status().isOk() ).andReturn();
        final String staffResultString = staffResult.getResponse().getContentAsString();
        final String[] staffResultParsed = staffResultString.split( "," );
        assertEquals( 14, staffResultParsed.length );
        System.out.println( staffResultString );

        final MvcResult customerResult = mvc
                .perform( get( "/api/v1/order" ).contentType( MediaType.APPLICATION_JSON ).cookie(
                        new Cookie( "sessionid", customerDetails.get( "sessionid" ) ),
                        new Cookie( "username", customerDetails.get( "username" ) ) ) )
                .andExpect( status().isOk() ).andReturn();
        final String customerResultString = customerResult.getResponse().getContentAsString();
        final String[] customerResultParsed = customerResultString.split( "," );
        assertEquals( 7, customerResultParsed.length );
    }

    @Test
    @Transactional
    public void testFulfillOrders () throws Exception {
        final Map<String, Integer> orderContents = new HashMap<String, Integer>();
        orderContents.put( recipe.getName(), 1 );
        orderContents.put( recipe2.getName(), 2 );

        mvc.perform( put( "/api/v1/order" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( orderContents ) )
                .cookie( new Cookie( "sessionid", customerDetails.get( "sessionid" ) ),
                        new Cookie( "username", customerDetails.get( "username" ) ) ) )
                .andExpect( status().isOk() );

        final Long id = Long.valueOf( oService.findAll().get( 0 ).getId().toString() );
        final Map<String, Long> tempMap = new TreeMap<String, Long>();
        tempMap.put( "id", id );

        mvc.perform( post( "/api/v1/order/fulfill" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( tempMap ) )
                .cookie( new Cookie( "sessionid", staffDetails.get( "sessionid" ) ),
                        new Cookie( "username", staffDetails.get( "username" ) ) ) )
                .andExpect( status().isOk() );

        assertEquals( 8, iService.getInventory().getIngredient( "A" ) );
    }

    @Test
    @Transactional
    public void testPickupOrders () throws Exception {
        final Map<String, Integer> orderContents = new HashMap<String, Integer>();
        orderContents.put( recipe.getName(), 1 );
        orderContents.put( recipe2.getName(), 2 );

        mvc.perform( put( "/api/v1/order" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( orderContents ) )
                .cookie( new Cookie( "sessionid", customerDetails.get( "sessionid" ) ),
                        new Cookie( "username", customerDetails.get( "username" ) ) ) )
                .andExpect( status().isOk() );

        final Long id = Long.valueOf( oService.findAll().get( 0 ).getId().toString() );
        final Map<String, Long> tempMap = new TreeMap<String, Long>();
        tempMap.put( "id", id );

        mvc.perform( post( "/api/v1/order/fulfill" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( tempMap ) )
                .cookie( new Cookie( "sessionid", staffDetails.get( "sessionid" ) ),
                        new Cookie( "username", staffDetails.get( "username" ) ) ) )
                .andExpect( status().isOk() );

        mvc.perform( post( "/api/v1/order/pickup" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( tempMap ) )
                .cookie( new Cookie( "sessionid", customerDetails.get( "sessionid" ) ),
                        new Cookie( "username", customerDetails.get( "username" ) ) ) )
                .andExpect( status().isOk() );
        // assertions.assertTrue(oService.findById( id ).isPickedUp());
        Assertions.assertTrue( oService.findById( id ).isPickedUp() );
    }
}
