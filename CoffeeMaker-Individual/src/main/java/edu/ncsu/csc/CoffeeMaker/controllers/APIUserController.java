package edu.ncsu.csc.CoffeeMaker.controllers;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.CoffeeMaker.models.CoffeemakerUser;
import edu.ncsu.csc.CoffeeMaker.models.enums.CoffeemakerUserType;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

/**
 * This is the controller that holds the REST endpoints that handle CRUD
 * operations for Recipes.
 *
 * Spring will automatically convert all of the ResponseEntity and List results
 * to JSON
 *
 * @author Kai Presler-Marshall
 * @author Michelle Lemons
 *
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APIUserController extends APIController {

    /**
     * UserService object, to be autowired in by Spring to allow for
     * manipulating the User model
     */
    @Autowired
    private UserService service;

    /**
     * REST API method to provide GET access to a specific user for login, as
     * indicated by the path variable provided
     *
     * @param json
     *            Json of user to login (username, password)
     *
     * @return response to the request
     */
    @GetMapping ( BASE_PATH + "/user/login" )
    public ResponseEntity loginUser ( @RequestBody final Map<String, String> json,
            final HttpServletResponse response ) {
        final CoffeemakerUser user = service.findByName( json.get( "username" ) );
        if ( user == null ) {
            return new ResponseEntity( errorResponse( "Username incorrect" ), HttpStatus.NOT_FOUND );
        }
        if ( user.compareHash( json.get( "password" ) ) ) {
            final String id = user.login();
            service.save( user );
            final Cookie cookieSessionID = new Cookie( "sessionid", id );
            cookieSessionID.setHttpOnly( true );
            cookieSessionID.setSecure( true );
            cookieSessionID.setDomain( "localhost" );
            final Cookie cookieUsername = new Cookie( "username", json.get( "username" ) );
            cookieUsername.setHttpOnly( true );
            cookieUsername.setSecure( true );
            cookieUsername.setDomain( "localhost" );
            final Cookie cookieType = new Cookie( "type",
                    user.getUserType() == CoffeemakerUserType.Customer ? "Customer" : "Staff" );
            cookieType.setHttpOnly( false );
            cookieType.setSecure( true );
            cookieType.setDomain( "localhost" );
            response.addCookie( cookieSessionID );
            response.addCookie( cookieUsername );
            response.addCookie( cookieType );
            return new ResponseEntity( HttpStatus.OK );
        }
        else {
            return new ResponseEntity( errorResponse( "Incorrect password" ), HttpStatus.UNAUTHORIZED );
        }
    }

    /**
     * REST API method to provide GET access to a specific user for logout, as
     * indicated by the path variable provided
     *
     * @param json
     *            Json of user to login (username, password)
     *
     * @return response to the request
     */
    @GetMapping ( BASE_PATH + "/user/logout" )
    public ResponseEntity logoutUser ( @CookieValue ( "username" ) final String username,
            @CookieValue ( "sessionid" ) final String sessionid ) {
        final CoffeemakerUser user = service.findByName( username );
        if ( user == null ) {
            return new ResponseEntity( errorResponse( "Username incorrect" ), HttpStatus.NOT_FOUND );
        }
        if ( user.compareSessionId( sessionid ) ) {
            user.logout();
            service.save( user );
            return new ResponseEntity( "Logged out", HttpStatus.OK );
        }
        else {
            return new ResponseEntity( errorResponse( "Incorrect session ID, possibly expired or already logged out." ),
                    HttpStatus.UNAUTHORIZED );
        }
    }

    /**
     * REST API method to provide POST access to the user model. This is used to
     * create a new user by automatically converting the JSON RequestBody
     * provided to a user object. Invalid JSON will fail.
     *
     * @param json
     *            Json (username, password, type (Staff/Customer) of the new
     *            user
     * @return ResponseEntity indicating success if the User could be saved to
     *         the database, or an error if it could not be
     */
    @PostMapping ( BASE_PATH + "/user" )
    public ResponseEntity makeUser ( @RequestBody final Map<String, String> json, final HttpServletResponse response ) {
        CoffeemakerUser user;
        if ( json.get( "type" ).equals( "Staff" ) ) {
            user = new CoffeemakerUser( json.get( "username" ), json.get( "password" ), "Staff" );
        }
        else if ( json.get( "type" ).equals( "Customer" ) ) {
            user = new CoffeemakerUser( json.get( "username" ), json.get( "password" ), "Customer" );
        }
        else {
            return new ResponseEntity( errorResponse( "bad user type" ), HttpStatus.BAD_REQUEST );
        }
        if ( service.findByName( json.get( "username" ) ) != null ) {
            return new ResponseEntity( errorResponse( "Username taken" ), HttpStatus.CONFLICT );
        }
        final String id = user.login();
        service.save( user );
        final Cookie cookieSessionID = new Cookie( "sessionid", id );
        cookieSessionID.setHttpOnly( true );
        cookieSessionID.setSecure( true );
        cookieSessionID.setDomain( "localhost" );
        final Cookie cookieUsername = new Cookie( "username", json.get( "username" ) );
        cookieUsername.setHttpOnly( false );
        cookieUsername.setSecure( true );
        cookieUsername.setDomain( "localhost" );
        final Cookie cookieType = new Cookie( "type",
                user.getUserType() == CoffeemakerUserType.Customer ? "Customer" : "Staff" );
        cookieType.setHttpOnly( false );
        cookieType.setSecure( true );
        cookieType.setDomain( "localhost" );
        response.addCookie( cookieSessionID );
        response.addCookie( cookieUsername );
        response.addCookie( cookieType );
        return new ResponseEntity( HttpStatus.OK );
    }
}
