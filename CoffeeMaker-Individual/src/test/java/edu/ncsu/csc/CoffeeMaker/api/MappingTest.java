/**
 *
 */
package edu.ncsu.csc.CoffeeMaker.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.Cookie;
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
import org.springframework.test.web.servlet.MvcResult;

import edu.ncsu.csc.CoffeeMaker.common.TestUtils;

/**
 * @author zuhar Tests MappingController class
 */
@ExtendWith ( SpringExtension.class )
@SpringBootTest
@AutoConfigureMockMvc
public class MappingTest {

    @Autowired
    private MockMvc     mvc;

    Map<String, String> customerDetails;

    Map<String, String> customer2Details;

    Map<String, String> staffDetails;

    /**
     * Sets up the tests.
     *
     * @throws Exception
     */
    @BeforeEach
    public void setup () throws Exception {

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

    /**
     * Tests login
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void testLogin () throws Exception {

        final MvcResult result = mvc
                .perform( get( "/login.html" ).contentType( MediaType.APPLICATION_JSON ).cookie(
                        new Cookie( "username", customerDetails.get( "username" ) ),
                        new Cookie( "sessionid", customerDetails.get( "sessionid" ) ),
                        new Cookie( "type", customerDetails.get( "type" ) ) ) )
                .andExpect( status().isOk() ).andReturn();

        result.equals( result );

    }

    /**
     * Tests add recipe
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void testAddRecipe () throws Exception {

        final MvcResult result = mvc
                .perform( get( "/recipe.html" ).contentType( MediaType.APPLICATION_JSON ).cookie(
                        new Cookie( "username", customerDetails.get( "username" ) ),
                        new Cookie( "sessionid", customerDetails.get( "sessionid" ) ),
                        new Cookie( "type", customerDetails.get( "type" ) ) ) )
                .andExpect( status().isOk() ).andReturn();

        result.equals( result );

    }

    /**
     * Tests delete recipe
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void testDeleteRecipe () throws Exception {

        final MvcResult result = mvc
                .perform( get( "/deleterecipe.html" ).contentType( MediaType.APPLICATION_JSON ).cookie(
                        new Cookie( "username", customerDetails.get( "username" ) ),
                        new Cookie( "sessionid", customerDetails.get( "sessionid" ) ),
                        new Cookie( "type", customerDetails.get( "type" ) ) ) )
                .andExpect( status().isOk() ).andReturn();

        result.equals( result );

    }

    /**
     * Tests edit recipe
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void testEditRecipe () throws Exception {

        final MvcResult result = mvc
                .perform( get( "/editrecipe.html" ).contentType( MediaType.APPLICATION_JSON ).cookie(
                        new Cookie( "username", customerDetails.get( "username" ) ),
                        new Cookie( "sessionid", customerDetails.get( "sessionid" ) ),
                        new Cookie( "type", customerDetails.get( "type" ) ) ) )
                .andExpect( status().isOk() ).andReturn();

        result.equals( result );

    }

    /**
     * Tests inventory form
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void testInventoryForm () throws Exception {

        final MvcResult result = mvc
                .perform( get( "/inventory.html" ).contentType( MediaType.APPLICATION_JSON ).cookie(
                        new Cookie( "username", customerDetails.get( "username" ) ),
                        new Cookie( "sessionid", customerDetails.get( "sessionid" ) ),
                        new Cookie( "type", customerDetails.get( "type" ) ) ) )
                .andExpect( status().isOk() ).andReturn();

        result.equals( result );

    }

    /**
     * Tests makecoffee form
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void testMakeCoffeeForm () throws Exception {

        final MvcResult result = mvc
                .perform( get( "/makecoffee.html" ).contentType( MediaType.APPLICATION_JSON ).cookie(
                        new Cookie( "username", customerDetails.get( "username" ) ),
                        new Cookie( "sessionid", customerDetails.get( "sessionid" ) ),
                        new Cookie( "type", customerDetails.get( "type" ) ) ) )
                .andExpect( status().isOk() ).andReturn();

        result.equals( result );

    }

    /**
     * Tests addingredients form
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void testAddIngredientsForm () throws Exception {

        final MvcResult result = mvc
                .perform( get( "/addingredients.html" ).contentType( MediaType.APPLICATION_JSON ).cookie(
                        new Cookie( "username", customerDetails.get( "username" ) ),
                        new Cookie( "sessionid", customerDetails.get( "sessionid" ) ),
                        new Cookie( "type", customerDetails.get( "type" ) ) ) )
                .andExpect( status().isOk() ).andReturn();

        result.equals( result );

    }

    /**
     * Tests sendorder form
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void testSendOrderForm () throws Exception {

        final MvcResult result = mvc
                .perform( get( "/sendorder.html" ).contentType( MediaType.APPLICATION_JSON ).cookie(
                        new Cookie( "username", customerDetails.get( "username" ) ),
                        new Cookie( "sessionid", customerDetails.get( "sessionid" ) ),
                        new Cookie( "type", customerDetails.get( "type" ) ) ) )
                .andExpect( status().isOk() ).andReturn();

        result.equals( result );

    }

    /**
     * Tests staff form
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void testStaffForm () throws Exception {

        final MvcResult result = mvc
                .perform( get( "/staff.html" ).contentType( MediaType.APPLICATION_JSON ).cookie(
                        new Cookie( "username", customerDetails.get( "username" ) ),
                        new Cookie( "sessionid", customerDetails.get( "sessionid" ) ),
                        new Cookie( "type", customerDetails.get( "type" ) ) ) )
                .andExpect( status().isOk() ).andReturn();

        result.equals( result );

    }

    /**
     * Tests history form
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void testHistoryForm () throws Exception {

        final MvcResult result = mvc
                .perform( get( "/history.html" ).contentType( MediaType.APPLICATION_JSON ).cookie(
                        new Cookie( "username", customerDetails.get( "username" ) ),
                        new Cookie( "sessionid", customerDetails.get( "sessionid" ) ),
                        new Cookie( "type", customerDetails.get( "type" ) ) ) )
                .andExpect( status().isOk() ).andReturn();

        result.equals( result );

    }

    /**
     * Tests customer form
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void testCustomerForm () throws Exception {

        final MvcResult result = mvc
                .perform( get( "/customer.html" ).contentType( MediaType.APPLICATION_JSON ).cookie(
                        new Cookie( "username", customerDetails.get( "username" ) ),
                        new Cookie( "sessionid", customerDetails.get( "sessionid" ) ),
                        new Cookie( "type", customerDetails.get( "type" ) ) ) )
                .andExpect( status().isOk() ).andReturn();

        result.equals( result );

    }

    /**
     * Tests vieworder form
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void testViewOrderForm () throws Exception {

        final MvcResult result = mvc
                .perform( get( "/vieworder.html" ).contentType( MediaType.APPLICATION_JSON ).cookie(
                        new Cookie( "username", customerDetails.get( "username" ) ),
                        new Cookie( "sessionid", customerDetails.get( "sessionid" ) ),
                        new Cookie( "type", customerDetails.get( "type" ) ) ) )
                .andExpect( status().isOk() ).andReturn();

        result.equals( result );

    }

}
