package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.CoffeeMaker.common.TestUtils;
import edu.ncsu.csc.CoffeeMaker.models.CoffeemakerUser;
import edu.ncsu.csc.CoffeeMaker.models.enums.CoffeemakerUserType;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith ( SpringExtension.class )
/**
 * This test class test the login(), logout(), and makeUser() API endpoints used
 * by the APIUserController class to perform UC8, UC9, and UC10
 *
 * @author gtbachel
 *
 */
public class APIUserTest {

    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    /** Context used for attaching to web application */
    @Autowired
    private WebApplicationContext context;

    /** User service used to communicate with the User repository */
    @Autowired
    private UserService           service;

    /**
     * Sets up the tests. Removes any items in the database
     */
    @BeforeEach
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

        service.deleteAll();
    }

    /**
     * This method tests the POST API call that creates new users into the
     * database.
     *
     * @throws Exception
     *             If something goes wrong during testing
     */
    @Test
    @Transactional
    public void testMakeUser () throws Exception {
        // Create tree map to hold the user information
        final Map<String, String> user = new TreeMap<String, String>();
        // Set the username for the user
        user.put( "username", "user" );
        // Set the password for the user
        user.put( "password", "pass" );
        // Set the user type for the user
        user.put( "type", "Staff" );

        // Make the POST API call and check for a IsOK status returned
        mvc.perform( post( "/api/v1/user" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( user ) ) ).andExpect( status().isOk() );

        // This variable will store the saved user in the database
        final CoffeemakerUser savedUser = service.findByName( "user" );
        // Verify the user type was stores correctly
        assertEquals( savedUser.getUserType(), CoffeemakerUserType.Staff );
        // Check that the password was hash properly
        assertTrue( savedUser.compareHash( "pass" ) );

        // Create new user with different information
        user.put( "username", "user2" );
        user.put( "password", "pass2" );
        user.put( "type", "Customer" );

        /// Make the POST API call and check that is work correctly
        mvc.perform( post( "/api/v1/user" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( user ) ) ).andExpect( status().isOk() );
        // Hold the new user in this variable
        final CoffeemakerUser savedUser2 = service.findByName( "user2" );
        // Verify the user type
        assertEquals( savedUser2.getUserType(), CoffeemakerUserType.Customer );
        // Verify the password
        assertTrue( savedUser2.compareHash( "pass2" ) );

        // Create a copy of the second user
        user.put( "username", "user2" );
        user.put( "password", "pass2" );
        user.put( "type", "Huh" );

        // Make the POST API call and make sure it is rejected.
        mvc.perform( post( "/api/v1/user" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( user ) ) ).andExpect( status().isBadRequest() );
    }

    /**
     * This test method test the login and logout API calls
     *
     * @throws Exception
     *             If error occurs in testing
     */
    @Test
    @Transactional
    public void testLoginLogout () throws Exception {

        // Create test user
        final CoffeemakerUser user = new CoffeemakerUser( "user", "pass", "Staff" );
        // Save user to database
        service.save( user );
        // Use tree map to create test user
        final Map<String, String> userDetails = new TreeMap<String, String>();

        // Fill in the same credentials as the saved user
        userDetails.put( "username", "user" );
        userDetails.put( "password", "pass" );

        // Make the POST API call and verify that the login was a success
        final MvcResult result = mvc.perform( post( "/api/v1/user/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( userDetails ) ) ).andExpect( status().isOk() ).andReturn();

        // Store the session ID
        final String id = result.getResponse().getCookie( "sessionid" ).getValue();

        // Verify it is the correct session id for the logged in user
        assertTrue( user.compareSessionId( id ) );

        // Add session id to test user
        userDetails.put( "sessionid", id );
        // Remove password from tree
        userDetails.remove( "password" );

        // Test that the logout call was successful
        mvc.perform( get( "/api/v1/user/logout" ).contentType( MediaType.APPLICATION_JSON )
                .cookie( result.getResponse().getCookies() ) ).andExpect( status().isOk() ).andReturn();

        // Make sure session id is updated to not match prior value
        assertTrue( !user.compareSessionId( id ) );

        // Create a user without a session id
        userDetails.put( "username", "user" );
        userDetails.put( "password", "pasz" );
        userDetails.remove( "sessionid" );

        // Try to login in without a session id, should return an unauthorized
        // status
        mvc.perform( post( "/api/v1/user/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( userDetails ) ) ).andExpect( status().isUnauthorized() );
        // Change user =name and password
        userDetails.put( "username", "funny" );
        userDetails.put( "password", "funny" );
        // Attempt to log in user that is not in system
        mvc.perform( post( "/api/v1/user/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( userDetails ) ) ).andExpect( status().isNotFound() );

        final Cookie session = result.getResponse().getCookie( "username" );
        // Change the session id to a mismatching value
        session.setValue( "funny" );
        // try to log out a user with an invalid session id
        mvc.perform( get( "/api/v1/user/logout" ).contentType( MediaType.APPLICATION_JSON )
                .cookie( result.getResponse().getCookie( "sessionid" ), session ) ).andExpect( status().isNotFound() );
    }
}
