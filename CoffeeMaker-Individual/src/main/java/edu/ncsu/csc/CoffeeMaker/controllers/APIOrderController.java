package edu.ncsu.csc.CoffeeMaker.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.CoffeeMaker.models.CoffeemakerOrder;
import edu.ncsu.csc.CoffeeMaker.models.CoffeemakerUser;
import edu.ncsu.csc.CoffeeMaker.models.enums.CoffeemakerUserType;
import edu.ncsu.csc.CoffeeMaker.models.enums.Permissions;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;
import edu.ncsu.csc.CoffeeMaker.services.OrderQueueService;
import edu.ncsu.csc.CoffeeMaker.services.OrderService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

/**
 *
 * The APICoffeeController is responsible for making coffee when a user submits
 * a request to do so.
 *
 * Spring will automatically convert all of the ResponseEntity and List results
 * to JSON
 *
 * @author Kai Presler-Marshall
 *
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APIOrderController extends APIController {

    /**
     * InventoryService object, to be autowired in by Spring to allow for
     * manipulating the Inventory model
     */
    @Autowired
    private InventoryService  inventoryService;

    @Autowired
    private OrderService      orderService;

    @Autowired
    private OrderQueueService orderQueueService;
    @Autowired
    private UserService       userService;

    @Autowired
    private RecipeService     recipeService;

    /**
     * REST API method to make coffee by completing a POST request with the ID
     * of the recipe as the path variable and the amount that has been paid as
     * the body of the response
     *
     * @param name
     *            recipe name
     * @param amtPaid
     *            amount paid
     * @return The change the customer is due if successful
     */
    @GetMapping ( BASE_PATH + "/order" )
    public ResponseEntity getOrders ( @CookieValue ( "username" ) final String username,
            @CookieValue ( "sessionid" ) final String sessionid ) {
        final CoffeemakerUser user = userService.findByName( username );
        final List<CoffeemakerOrder> orderList;
        if ( user == null ) {
            return new ResponseEntity( errorResponse( "Username incorrect" ), HttpStatus.NOT_FOUND );
        }
        if ( user.compareSessionId( sessionid ) ) {
            if ( user.getUserType() == CoffeemakerUserType.Staff ) {
                orderList = orderQueueService.getOrderQueue().getOrders();
            }
            else {
                orderList = new ArrayList<CoffeemakerOrder>();
                for ( final CoffeemakerOrder order : orderQueueService.getOrderQueue().getOrders() ) {
                    if ( order.getUsername().equals( user.getName() ) ) {
                        orderList.add( order );
                    }
                }
            }
        }
        else {
            return new ResponseEntity( errorResponse( "Incorrect session ID, possibly expired or already logged out." ),
                    HttpStatus.UNAUTHORIZED );
        }

        return new ResponseEntity( orderList, HttpStatus.OK );
    }

    @PutMapping ( BASE_PATH + "/order" )
    public ResponseEntity createOrder ( @RequestBody final Map<String, Integer> recipes,
            @CookieValue ( "username" ) final String username, @CookieValue ( "sessionid" ) final String sessionid ) {
        final CoffeemakerUser user = userService.findByName( username );
        if ( user == null ) {
            return new ResponseEntity( errorResponse( "Username incorrect" ), HttpStatus.NOT_FOUND );
        }
        if ( !user.compareSessionId( sessionid ) ) {
            return new ResponseEntity( errorResponse( "Incorrect session ID, possibly expired or already logged out." ),
                    HttpStatus.UNAUTHORIZED );
        }
        final CoffeemakerOrder order = new CoffeemakerOrder( recipes );
        order.create( username );
        orderQueueService.getOrderQueue().placeOrder( order );
        orderService.save( order );
        return new ResponseEntity( order.getId(), HttpStatus.OK );
    }

    @PostMapping ( BASE_PATH + "/order/fulfill" )
    public ResponseEntity fulfillOrder ( @RequestBody final Map<String, Long> id,
            @CookieValue ( "username" ) final String username, @CookieValue ( "sessionid" ) final String sessionid ) {
        final CoffeemakerUser user = userService.findByName( username );
        if ( user == null ) {
            return new ResponseEntity( errorResponse( "Username incorrect" ), HttpStatus.NOT_FOUND );
        }
        if ( !user.compareSessionId( sessionid ) ) {
            return new ResponseEntity( errorResponse( "Incorrect session ID, possibly expired or already logged out." ),
                    HttpStatus.UNAUTHORIZED );
        }
        if ( !user.hasPermission( Permissions.CanFulfillOrder ) ) {
            return new ResponseEntity( errorResponse( "Invalid permissions" ), HttpStatus.UNAUTHORIZED );
        }
        final CoffeemakerOrder order = orderService.findById( id.get( "id" ) );
        order.fullfil();
        for ( final String recipeName : order.getRecipes().keySet() ) {
            for ( int i = 0; i < order.getRecipes().get( recipeName ); i++ ) {
                inventoryService.getInventory().useIngredients( recipeService.findByName( recipeName ) );
            }
        }
        return new ResponseEntity( HttpStatus.OK );
    }

    @PostMapping ( BASE_PATH + "/order/pickup" )
    public ResponseEntity pickupOrder ( @RequestBody final Map<String, Long> id,
            @CookieValue ( "username" ) final String username, @CookieValue ( "sessionid" ) final String sessionid ) {
        final CoffeemakerUser user = userService.findByName( username );
        if ( user == null ) {
            return new ResponseEntity( errorResponse( "Username incorrect" ), HttpStatus.NOT_FOUND );
        }
        if ( !user.compareSessionId( sessionid ) ) {
            return new ResponseEntity( errorResponse( "Incorrect session ID, possibly expired or already logged out." ),
                    HttpStatus.UNAUTHORIZED );
        }
        if ( !user.hasPermission( Permissions.CanPickupOrder ) ) {
            return new ResponseEntity( errorResponse( "Invalid permissions" ), HttpStatus.UNAUTHORIZED );
        }
        final CoffeemakerOrder order = orderService.findById( id.get( "id" ) );
        if ( !order.getUsername().equals( user.getName() ) ) {
            return new ResponseEntity( errorResponse( "Invalid permissions" ), HttpStatus.UNAUTHORIZED );
        }
        order.pickup();

        return new ResponseEntity( HttpStatus.OK );
    }
}
