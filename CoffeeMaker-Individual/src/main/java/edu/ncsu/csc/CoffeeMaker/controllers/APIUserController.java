package edu.ncsu.csc.CoffeeMaker.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.CoffeeMaker.models.CoffeemakerCustomer;
import edu.ncsu.csc.CoffeeMaker.models.CoffeemakerStaff;
import edu.ncsu.csc.CoffeeMaker.models.CoffeemakerUser;
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
     * REST API method to provide GET access to a specific user, as indicated by
     * the path variable provided (the name of the recipe desired)
     *
     * @param json
     *            Json of user to login (username, password)
     *
     * @return response to the request
     */
    @GetMapping ( BASE_PATH + "/user" )
    public ResponseEntity loginUser ( @RequestBody final Map<String, String> json ) {
        final CoffeemakerUser user = service.findByName( json.get( "username" ) );
        if ( user == null ) {
            new ResponseEntity( errorResponse( "Username incorrect" ), HttpStatus.NOT_FOUND );
        }
        if ( user.compareHash( json.get( "password" ) ) ) {
            return new ResponseEntity( user.login(), HttpStatus.OK );
        }
        else {
            new ResponseEntity( errorResponse( "Incorrect password" ), HttpStatus.UNAUTHORIZED );
        }
        return new ResponseEntity( errorResponse( "Login error" ), HttpStatus.BAD_REQUEST );
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
     *         the inventory, or an error if it could not be
     */
    @PostMapping ( BASE_PATH + "/user" )
    public ResponseEntity makeUser ( @RequestBody final Map<String, String> json ) {
        CoffeemakerUser user;
        if ( json.get( "type" ).equals( "Staff" ) ) {
            user = new CoffeemakerStaff( json.get( "username" ), json.get( "password" ) );
        }
        else if ( json.get( "type" ).equals( "Customer" ) ) {
            user = new CoffeemakerCustomer( json.get( "username" ), json.get( "password" ) );
        }
        else {
            return new ResponseEntity( errorResponse( "bad user type" ), HttpStatus.BAD_REQUEST );
        }
        service.save( user );
        return new ResponseEntity( user.login(), HttpStatus.OK );
    }
}
