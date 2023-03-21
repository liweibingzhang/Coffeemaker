package edu.ncsu.csc.CoffeeMaker.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

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

import edu.ncsu.csc.CoffeeMaker.common.TestUtils;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

@ExtendWith ( SpringExtension.class )
@SpringBootTest
@AutoConfigureMockMvc
public class APICoffeeTest {

    @Autowired
    private MockMvc          mvc;

    @Autowired
    private RecipeService    service;

    @Autowired
    private InventoryService iService;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        service.deleteAll();
        iService.deleteAll();

        final Inventory ivt = iService.getInventory();

        ivt.setIngredient( "Chocolate", 15 );
        ivt.setIngredient( "Coffee", 15 );
        ivt.setIngredient( "Milk", 15 );
        ivt.setIngredient( "Sugar", 15 );
        ivt.setIngredient( "Oat Milk", 25 );

        iService.save( ivt );

        final Recipe recipe = new Recipe( "Coffee", new ArrayList<Ingredient>(), 50 );
        recipe.addIngredient( new Ingredient( "Coffee", 3 ) );
        recipe.addIngredient( new Ingredient( "Milk", 1 ) );
        recipe.addIngredient( new Ingredient( "Sugar", 1 ) );
        recipe.addIngredient( new Ingredient( "Chocolate", 0 ) );

        service.save( recipe );

        final Recipe veganRecipe = new Recipe( "Vegan Coffee", new ArrayList<Ingredient>(), 50 );
        veganRecipe.addIngredient( new Ingredient( "Coffee", 3 ) );
        veganRecipe.addIngredient( new Ingredient( "Oat Milk", 1 ) );
        veganRecipe.addIngredient( new Ingredient( "Sugar", 1 ) );
        veganRecipe.addIngredient( new Ingredient( "Chocolate", 0 ) );

        service.save( veganRecipe );

    }

    @Test
    @Transactional
    public void testPurchaseBeverage1 () throws Exception {

        final String name = "Coffee";

        mvc.perform( post( String.format( "/api/v1/makecoffee/%s", name ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( 60 ) ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.message" ).value( 10 ) );

    }

    @Test
    @Transactional
    public void testPurchaseBeverage2 () throws Exception {
        /* Insufficient amount paid */

        final String name = "Coffee";

        mvc.perform( post( String.format( "/api/v1/makecoffee/%s", name ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( 40 ) ) ).andExpect( status().is4xxClientError() )
                .andExpect( jsonPath( "$.message" ).value( "Not enough money paid" ) );

    }

    @Test
    @Transactional
    public void testPurchaseBeverage3 () throws Exception {
        /* Insufficient inventory */

        final Inventory ivt = iService.getInventory();
        ivt.setIngredient( "Coffee", 0 );
        iService.save( ivt );

        final String name = "Coffee";

        mvc.perform( post( String.format( "/api/v1/makecoffee/%s", name ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( 50 ) ) ).andExpect( status().is4xxClientError() )
                .andExpect( jsonPath( "$.message" ).value( "Not enough inventory" ) );

    }

    @Test
    @Transactional
    public void testPurchaseBeverage4 () throws Exception {
        /* No Recipe Selected */

        final Inventory ivt = iService.getInventory();
        ivt.setIngredient( "Coffee", 0 );
        iService.save( ivt );

        final String name = "InvalidCoffee";

        mvc.perform( post( String.format( "/api/v1/makecoffee/%s", name ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( 50 ) ) ).andExpect( status().is4xxClientError() )
                .andExpect( jsonPath( "$.message" ).value( "No recipe selected" ) );

    }

    @Test
    @Transactional
    public void testPurchaseBeverage5 () throws Exception {

        final String name = "Vegan Coffee";

        mvc.perform( post( String.format( "/api/v1/makecoffee/%s", name ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( 60 ) ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.message" ).value( 10 ) );

    }

}
