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
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
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

    /**
     * OrderService object, to be autowired in by Spring to allow for
     * manipulating the Order models
     */
    @Autowired
    private OrderService      orderService;

    /**
     * OrderQueueService object, to be autowired in by Spring to allow for
     * manipulating the order queue model
     */
    @Autowired
    private OrderQueueService orderQueueService;

    /**
     * UserService object, to be autowired in by Spring to allow for
     * manipulating the user models
     */
    @Autowired
    private UserService       userService;

    /**
     * RecipeService object, to be autowired in by Spring to allow for
     * manipulating the recipe models
     */
    @Autowired
    private RecipeService     recipeService;

    /**
     * Endpoint to get the orders visible to the current signed in user. Staff
     * can see all orders, users can see their own orders
     *
     * @param username
     *            Username from signed in user.
     * @param sessionid
     *            Session id from signed in user
     * @return Either all orders or some subset belonging to a particular user
     *         depending on the account type calling this endpoint
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

    /**
     * Creates a new order with the given recipes and quantities.
     *
     * @param recipes
     *            Recipes with associated quantities in a map.
     * @param username
     *            Username of the user placing the order
     * @param sessionid
     *            Valid session id for the user named username
     * @return ID of the new order created
     */
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

    /**
     * Changes an order status to fulfilled.
     *
     * @param id
     *            ID of the order to update
     * @param username
     *            Username of the staff fulfilling the order
     * @param sessionid
     *            SessionID of the staff fulfilling the order
     * @return OK Status if the order could be made using the ingredients in the
     *         inventory.
     */
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
        final Recipe totalRequirement = new Recipe( "THIS_SHOULD_NOT_BE_SAVED", new ArrayList<Ingredient>(), 5 );
        for ( final String recipeName : order.getRecipes().keySet() ) {
            for ( int i = 0; i < order.getRecipes().get( recipeName ); i++ ) {
                final Recipe tempRecipe = recipeService.findByName( recipeName );
                for ( final Ingredient ingredient : tempRecipe.getIngredients() ) {
                    try {
                        final Ingredient newIng = new Ingredient( ingredient.getName(), ingredient.getAmount() );
                        totalRequirement.addIngredient( newIng );
                    }
                    catch ( final Exception e ) {
                        totalRequirement.setIngredientAmount( ingredient.getName(),
                                totalRequirement.getIngredient( ingredient.getName() ).getAmount()
                                        + ingredient.getAmount() );
                    }
                }
            }
        }
        if ( !inventoryService.getInventory().enoughIngredients( totalRequirement ) ) {
            return new ResponseEntity( errorResponse( "Not enough ingredients" ), HttpStatus.PRECONDITION_FAILED );
        }
        order.fullfil();
        final Inventory inv = inventoryService.getInventory();
        orderService.save( order );
        for ( final String recipeName : order.getRecipes().keySet() ) {
            for ( int i = 0; i < order.getRecipes().get( recipeName ); i++ ) {
                inv.useIngredients( recipeService.findByName( recipeName ) );
            }
        }
        inventoryService.save( inv );
        return new ResponseEntity( HttpStatus.OK );
    }

    /**
     * Changes an order status to fulfilled.
     *
     * @param id
     *            ID of the order to update
     * @param username
     *            Username of the staff fulfilling the order
     * @param sessionid
     *            SessionID of the staff fulfilling the order
     * @return OK Status if the order belonged to the user trying to pick it up,
     *         and it has been fulfilled.
     */
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
        if ( !order.isFullfilled() ) {
            return new ResponseEntity( errorResponse( "Order cannot be picked up yet" ), HttpStatus.CONFLICT );
        }
        if ( order != null ) {
            order.pickup();
            orderService.save( order );
        }

        return new ResponseEntity( HttpStatus.OK );
    }
}
