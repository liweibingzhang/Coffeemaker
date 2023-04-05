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
public class APIUserTest {

    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserService           service;

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
    public void testMakeUser () throws Exception {
        service.deleteAll();

        final Map<String, String> user = new TreeMap<String, String>();

        user.put( "username", "user" );
        user.put( "password", "pass" );
        user.put( "type", "Staff" );

        mvc.perform( post( "/api/v1/user" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( user ) ) ).andExpect( status().isOk() );

        final CoffeemakerUser savedUser = service.findByName( "user" );
        assertEquals( savedUser.getUserType(), CoffeemakerUserType.Staff );
        assertTrue( savedUser.compareHash( "pass" ) );

        user.put( "username", "user2" );
        user.put( "password", "pass2" );
        user.put( "type", "Customer" );

        mvc.perform( post( "/api/v1/user" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( user ) ) ).andExpect( status().isOk() );

        final CoffeemakerUser savedUser2 = service.findByName( "user2" );
        assertEquals( savedUser2.getUserType(), CoffeemakerUserType.Customer );
        assertTrue( savedUser2.compareHash( "pass2" ) );

        user.put( "username", "user2" );
        user.put( "password", "pass2" );
        user.put( "type", "Huh" );
        mvc.perform( post( "/api/v1/user" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( user ) ) ).andExpect( status().isBadRequest() );
    }

    @Test
    @Transactional
    public void testLoginLogout () throws Exception {
        service.deleteAll();

        final CoffeemakerUser user = new CoffeemakerUser( "user", "pass", "Staff" );

        service.save( user );

        final Map<String, String> userDetails = new TreeMap<String, String>();

        userDetails.put( "username", "user" );
        userDetails.put( "password", "pass" );

        final MvcResult result = mvc.perform( get( "/api/v1/user/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( userDetails ) ) ).andExpect( status().isOk() ).andReturn();

        final String id = result.getResponse().getCookie( "sessionid" ).getValue();

        assertTrue( user.compareSessionId( id ) );

        userDetails.put( "sessionid", id );
        userDetails.remove( "password" );

        mvc.perform( get( "/api/v1/user/logout" ).contentType( MediaType.APPLICATION_JSON )
                .cookie( result.getResponse().getCookies() ) ).andExpect( status().isOk() ).andReturn();

        assertTrue( !user.compareSessionId( id ) );

        userDetails.put( "username", "user" );
        userDetails.put( "password", "pasz" );
        userDetails.remove( "sessionid" );

        mvc.perform( get( "/api/v1/user/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( userDetails ) ) ).andExpect( status().isUnauthorized() );

        userDetails.put( "username", "funny" );
        userDetails.put( "password", "funny" );
        mvc.perform( get( "/api/v1/user/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( userDetails ) ) ).andExpect( status().isNotFound() );
        final Cookie funnyCookie = result.getResponse().getCookie( "username" );
        funnyCookie.setValue( "funny" );
        mvc.perform( get( "/api/v1/user/logout" ).contentType( MediaType.APPLICATION_JSON )
                .cookie( result.getResponse().getCookie( "sessionid" ), funnyCookie ) )
                .andExpect( status().isNotFound() );
    }
}
